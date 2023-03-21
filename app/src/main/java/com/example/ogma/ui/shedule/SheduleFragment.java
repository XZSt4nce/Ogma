package com.example.ogma.ui.shedule;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ogma.databinding.FragmentSheduleBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SheduleFragment extends Fragment {

    private FragmentSheduleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SheduleViewModel sheduleViewModel =
                new ViewModelProvider(this).get(SheduleViewModel.class);

        new MyTask().execute();
        binding = FragmentSheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        sheduleViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("StaticFieldLeak")
    private static class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url;
            try {
                url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1&RaspBaseSearch%5Bgroup_id%5D=44&RaspBaseSearch%5Bsemestr%5D=vesna&RaspBaseSearch%5Bprepod_id%5D=");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            Document doc;
            try {
                doc = Jsoup.parse(url, 3000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements newsHeadlines = doc.getElementsByClass("card h-100");
            for (Element headline : newsHeadlines) {
                Log.d("FFFF", headline.html());
            }
            return "Done";
        }


        @Override
        protected void onPostExecute(String result) {
            // ignore
        }
    }
}