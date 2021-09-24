package vander.gabriel.intents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> parameter = new MutableLiveData<>();

    public LiveData<String> getParameter() {
        return parameter;
    }

    public void setParameter(String value) {
        parameter.setValue(value);
    }

}
