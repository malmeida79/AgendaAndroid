package com.matech.agenda;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.matech.agenda.converter.AlunoConverter;
import com.matech.agenda.dao.AlunoDAO;
import com.matech.agenda.modelo.Aluno;
import java.util.List;

/**
 * Created by prog_ on 11/02/2018.
 */

// foi alterado o ultimo parametro metodos do generics AsyncTask<Void, Void, String>
// para string para os fazer mais sentido e os demais como nao estamos usando
// passamos void para indicar que nao estamos precisando
public class EnviaAlunosTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Enviando alunos...", true, true);
    }

    @Override
    protected String doInBackground(Void... params) {

        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.BuscarAlunos();
        dao.close();

        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.ConverteParaJSON(alunos);

        WebClient client = new WebClient();
        String resposta = client.Post(json);

        return resposta;
    }

    @Override
    protected void onPostExecute(String resposta) {

        // verifica se existe uma caixa de dialogo, as vezes executou tao
        // rapido que nem criou
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }

}
