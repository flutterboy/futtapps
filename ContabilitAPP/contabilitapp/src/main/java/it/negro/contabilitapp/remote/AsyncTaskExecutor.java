package it.negro.contabilitapp.remote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncTaskExecutor {

    private Activity activity;
    private AsyncTaskDelegate delegate;
    private String message;
    private String title;
    private ProgressDialog progressDialog;

    public AsyncTaskExecutor(Activity activity, AsyncTaskDelegate delegate, String title, String message) {
        this.activity = activity;
        this.delegate = delegate;
        this.message = message;
        this.title = title;
    }

    public void executeTask(Object... params){
        new AsyncTask<Object, Object, Object>(){

            @Override
            protected Object doInBackground(Object... params) {
                return delegate.executeTask(params);
            }

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, title, message);
            }

            @Override
            protected void onPostExecute(Object result) {
                progressDialog.dismiss();
                delegate.onResult(result);
            }
        }.execute(params);
    }

}
