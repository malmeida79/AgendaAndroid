package com.matech.agenda;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Browser;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.matech.agenda.converter.AlunoConverter;
import com.matech.agenda.dao.AlunoDAO;
import com.matech.agenda.modelo.Aluno;

import java.util.List;

import adapter.AlunosAdapter;

public class ListaAlunosActivity extends AppCompatActivity {

    private  ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView)  findViewById(R.id.lista_alunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent alteraAluno = new Intent(ListaAlunosActivity.this, FormularioActivity.class);

                // passando dados de uma tela para outra, no nosso caso passando o
                // aluno selecionado. Foi necessario incluir na classe aluno a implementacao
                // serializable para a classe poder ser serializada
                alteraAluno.putExtra("aluno",aluno);
                startActivity(alteraAluno);
            }
        });

        Button botaoNovoAluno = (Button)findViewById(R.id.novo_aluno);
        botaoNovoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cadastraAluno = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(cadastraAluno);
            }
        });

        registerForContextMenu(listaAlunos);
    }

    private void CarregaLista()  {

        // buscando alunos no banco de dados
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.BuscarAlunos();
        dao.close();

        AlunosAdapter adapter = new AlunosAdapter(this,alunos);
        listaAlunos.setAdapter(adapter);
    }

    // Quando sai de uma tela para outra como da lista para cadastro, ao
    // voltar nao roda on create, entao criamos on resume para que seja
    // executada essa carga da lista
    @Override
    protected void onResume() {
        super.onResume();
        CarregaLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listagem, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch para identificar qual item do menu foi clicado.
        switch (item.getItemId()) {
            case R.id.menu_enviar_notas:
                // disparando thread secundaria para nao usar a principal
                // pois o android nao permite devido tempo que essa acao
                // pode levar
                new EnviaAlunosTask(this).execute();
                break;
            case R.id.menu_baixar_provas:
                Intent vaiParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(vaiParaProvas);
                break;
            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this, MapaActivity.class);
                startActivity(vaiParaMapa);
                break;
            case R.id.menu_listagem_ok:
                MenuSobre();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        // Mostrando site cadastrado pelo aluno
        MenuItem itemSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);

        // recuperando site do aluno
        String site = aluno.getSite();
        if (!site.startsWith("https://")) {
            site = "https://" + site;
        }

        // abrindo para site
        intentSite.setData(Uri.parse(aluno.getSite()));
        itemSite.setIntent(intentSite);

        // Ligando para o aluno, foi necessário desa forma pois precisamos
        // inserir mais interações com o simples setintentity apenas
        // somos direcionados.
        MenuItem itemLigar = menu.add("Ligar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // verificando e pedindo a permissao do usuario
                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{ Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        // enviando sms para o aluno cadastrado
        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW) ;
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemSMS.setIntent(intentSMS);

        // mostrando aluno no mapa
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);

        // buscando latitude e do endereço do aluno
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        // deletar aluno caso se queira
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.Deletar(aluno);
                dao.close();

                CarregaLista();
                return false;

            }
        });


    }

    public void MenuSobre() {
        new AlertDialog.Builder(ListaAlunosActivity.this)
            .setTitle(":: Sobre o APP ::")
            .setMessage("Desenvolvido por: \n\nMarcos Almeida \nMBA em Gestão de Pessoas\nBch. em Sistemas de Informação \n\nTel: 11-970724528 \nEmail: prog.marcos@gmail.com")
            .setPositiveButton("Fechar",null)
            /* caso necessario alguma ação para o clique do botão
            .setPositiveButton("sim",
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
            })
            */
            /* caso quizessemos um botão não
            .setNegativeButton("não", null)
            */
            .show();

    }
    /*
    // controle de permissoes poderia ser dessa foma, nesse momento nao usaremos assim
    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantTesults) {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {
            // faz a ligacao
        }
        else if (requestCode == 124) {
            // envia SMS
        }
    }
    */
}
