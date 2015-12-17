package it.negro.contabilitapp.remote;

public interface AsyncTaskDelegate {

    Object executeTask (Object... params);
    void onResult(Object result);

}
