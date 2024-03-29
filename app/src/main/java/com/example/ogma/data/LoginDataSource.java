package com.example.ogma.data;

import com.example.ogma.data.model.LoggedInUser;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        String[] field = new String[] { "username", "password" };
        String[] data = new String[] { username, password };
        String putResult, fetchResult, id="", name="", lastName="", middleName="", role="", group="", email="", phone="", vk="", tg="", birthday="";
        FetchData fetchData = new FetchData("https://silentiumguard.com/c4f822fec11066d1ed15469717e6d9e1.php");
        if (fetchData.startFetch()) {
            if (fetchData.onComplete()) {
                fetchResult = fetchData.getResult();
                if (!fetchResult.equals("Success"))
                    return new Result.Error("Server connection error");
            }
        }

        PutData putData = new PutData("https://silentiumguard.com/d56b699830e77ba53855679cb1d252da.php", "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                putResult = putData.getResult().trim();

                if (putResult.isEmpty()) {
                    PutData putData2 = new PutData("https://silentiumguard.com/d56b699830e77ba53855679cb1d252da.php", "POST", field, data);
                    if (putData2.startPut()) {
                        if (putData2.onComplete()) {
                            putResult = putData2.getResult().trim();
                            if (putResult.isEmpty()) return new Result.Error("Wrong login or password");
                        }
                    }
                }

                id = putResult.split(" ")[0];
                name = putResult.split(" ")[1];
                lastName = putResult.split(" ")[2];
                middleName = putResult.split(" ")[3];
                role = putResult.split(" ")[4];
                group = putResult.split(" ")[5];
                email = putResult.split(" ")[6];
                phone = putResult.split(" ")[7];
                vk = putResult.split(" ")[8];
                tg = putResult.split(" ")[9];
                birthday = putResult.split(" ")[10];
            }
        }
        return new Result.Success<>(new LoggedInUser(id, name, lastName, middleName, role, group, email, phone, vk, tg, birthday));
    }

    public void logout() {
        // TODO: revoke authentication
    }
}