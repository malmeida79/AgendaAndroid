package com.matech.agenda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.matech.agenda.dao.AlunoDAO;
import com.matech.agenda.modelo.Aluno;

import java.io.File;
import java.security.AccessController;

import static android.support.v4.content.FileProvider.getUriForFile;

public class FormularioActivity extends AppCompatActivity {
    public static final int CODIGO_CAMERA = 567;
    private  FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        // recupear a intenty que chamou o formulario e
        // assim seus dados e o aluno
        Intent intent = getIntent();

        // recuperando a classe aluno que foi anexada na tela para edição
        Aluno aluno = (Aluno)intent.getSerializableExtra("aluno");

        // analizar se existir um aluno, então estamos em edição e assim sendo
        // tenho que preencher a tela com dados recuperados. caso nao apenas
        // abrir a tela.
        if(aluno!=null) {
            helper.PreencheFormulario(aluno);
        }

        // capturando e salvando fotos
        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/"+ System.currentTimeMillis() +".jpg";
                File arquivoFoto = new File(caminhoFoto);
                // foi necessario criar file provider, indicar no manifest, criar arquivo xml. etc devido ser android 7.0
                // ele nao permite acesso direto para salvar o arquivo.
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(FormularioActivity.this, "com.matech.agenda.fileprovider", arquivoFoto));
                startActivityForResult(intentCamera, CODIGO_CAMERA);
            }
        });
    }

    // escuta atividades como por exempo a camera e quando processo terminado
    // segue com ações
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // se resultado foto OK entao carregar a imagem caso nao nada fazer
        if(resultCode == Activity.RESULT_OK) {
            // se request que foi concluido for a camera entao tirar a foto
            if (requestCode == CODIGO_CAMERA) {
                // exbir a foto
                helper.CarregaImagem(caminhoFoto);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // infla o menu informando qual menu (xml e onde), no menu que ele recebe como
        // parametro (que é o menu da tela)
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch para identificar qual item do menu foi clicado.
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                // usando a classe aluno do modelo para passar os dados e capturar da tela
                // similar a model do mvc ou ent de app 3 camadas
                Aluno aluno = helper.PegaAluno();

                // buscando classe dao para o insert
                AlunoDAO dao = new AlunoDAO(this);

                // só tera ID quando estiver em edição de dados
                if (aluno.getId() != null)  {
                    // Alterando os dados do aluno
                    dao.Alterar(aluno);
                } else {
                    // gravando aluno no banco de dados
                    dao.Inserir(aluno);
                }

                // encerrando conexao, metodo close ja vem do sql lite
                dao.close();

                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " - " +aluno.getId() + ", salvo!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
