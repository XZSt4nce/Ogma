package com.example.ogma.data;

import com.example.ogma.data.model.LoggedInUser;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        String[] field = new String[]{"username", "password"};
        String[] data = new String[]{username, password};
        String putResult, fetchResult, id="", name="", lastName="", middleName="", role="", email="", phone="", vk="", tg="", birthday="";
        boolean success_connect = false;

        long end = System.currentTimeMillis()+1000;
        while (System.currentTimeMillis() < end) {
        PutData putData = new PutData("https://silentiumguard.com/d56b699830e77ba53855679cb1d252da.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    putResult = putData.getResult();
                    success_connect = true;
                    if (putResult.equals("Login Success")) {
                        FetchData fetchData = new FetchData(String.format("https://silentiumguard.com/c4f822fec11066d1ed15469717e6d9e1.php?username=%s&password=%s", username, password));
                        fetchData.startFetch();
                        fetchData.onComplete();
                        fetchResult = fetchData.getResult();
                        id = fetchResult.split(" ")[0];
                        name = fetchResult.split(" ")[1];
                        lastName = fetchResult.split(" ")[2];
                        middleName = fetchResult.split(" ")[3];
                        role = fetchResult.split(" ")[4];
                        email = fetchResult.split(" ")[5];
                        phone = fetchResult.split(" ")[6];
                        vk = fetchResult.split(" ")[7];
                        tg = fetchResult.split(" ")[8];
                        birthday = fetchResult.split(" ")[9];
                        break;
                    }
                }
            }
        }
        if (!id.isEmpty() && !name.isEmpty() && !role.isEmpty())
            return new Result.Success<>(new LoggedInUser(id, name, lastName, middleName, role, email, phone, vk, tg, birthday));
        else if (success_connect)
            return new Result.Error("Wrong login or password");
        else
            return new Result.Error("Server connection error");
    }

    public void logout() {
        // TODO: revoke authentication
    }
}