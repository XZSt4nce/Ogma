package com.example.ogma.ui.login;

import static com.example.ogma.data.HashPassword.encryptThisString;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ogma.R;
import com.example.ogma.data.LoginRepository;
import com.example.ogma.data.Result;
import com.example.ogma.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        if (username.isEmpty()) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        }
        else if (password.isEmpty()) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));

            Result<LoggedInUser> result = loginRepository.login(username, encryptThisString(password, username));
            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUID(), data.getDisplayName(), data.getLastName(), data.getMiddleName(), data.getRole(), data.getEmail(), data.getPhone(), data.getVk(), data.getTg(), data.getBirthday())));
            } else if (result.toString().equals("Error[exception=Server connection error]")) {
                loginResult.setValue(new LoginResult(R.string.connection_error));
            } else if (result.toString().equals("Error[exception=Wrong login or password]")) {
                loginResult.setValue(new LoginResult(R.string.invalid_username_or_password));
            }
        }
    }

    public void loginDataChanged(String username, String password) {
        if (username.isEmpty() && password.isEmpty()) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, R.string.invalid_password));
        }
        else if (username.isEmpty()) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        }
        else if (password.isEmpty()) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }
}