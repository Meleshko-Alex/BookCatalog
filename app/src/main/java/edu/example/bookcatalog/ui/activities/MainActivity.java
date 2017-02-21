package edu.example.bookcatalog.ui.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.kevinsawicki.http.HttpRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import edu.example.bookcatalog.R;
import edu.example.bookcatalog.data.BookModel;
import edu.example.bookcatalog.ui.adapters.RecyclerAdapter;
import edu.example.bookcatalog.utils.ConstantManager;

public class MainActivity extends BaseActivity {
    private ArrayList<BookModel> booksList;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler);

        if(savedInstanceState != null){
            booksList = (ArrayList<BookModel>) savedInstanceState.get(ConstantManager.INSTANCE_KEY);
            checkNetworkConnected();
            startRecycler();
        } else {
            checkNetworkConnected();
            showProgress();
            ParsePage pg = new ParsePage();
            pg.execute();
        }

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    class ParsePage extends AsyncTask<Void, Void, ArrayList<BookModel>>{

        ArrayList<BookModel> books = new ArrayList<>();
        @Override
        protected ArrayList<BookModel> doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect("http://epubbooks.ru/count.xml").get();
                Elements entryElements = document.select("entry");
                for(Element element : entryElements){
                    BookModel book = new BookModel(
                            element.select("title").text(),
                            element.select("content").text().

                            //Убираем лишнюю часть текста
                            substring(21, element.select("content").text().length()),

                            element.select("author").text(),
                            element.select("link").next().attr("href")
                    );
                    books.add(book);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<BookModel> books) {
            booksList = books;
            startRecycler();
            hideProgress();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ConstantManager.INSTANCE_KEY, booksList);
    }

    private void startRecycler(){
        mRecyclerAdapter = new RecyclerAdapter(getBaseContext(), booksList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                downloadFile();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Загрузка и сохранение картинки с сервера, а также провека и запрос Runtime Rermissions для
     * корректной работы на Android 6
     */
    private void downloadFile() {
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        && checkNetworkConnected() == true){

            DownloadAsyncTask dat = new DownloadAsyncTask();
            dat.execute();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работу необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * Проверяем есть ли соединение с сетью, если нет, то выводим диалог и возвращаем false
     */
    private boolean checkNetworkConnected() {
        boolean isConnect;
        String mMessage ;
        if(isBookListReceived()){
            mMessage = getString(R.string.check_and_try);
        } else {
            mMessage = getString(R.string.check_and_restart);
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.no_connect))
                    .setMessage(mMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(isBookListReceived()){
                                        dialog.cancel();
                                    }
                                    else finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            isConnect = false;
        } else isConnect = true;

        return isConnect;
    }

    /**
     * Пповеряем получили ли мы данны с сети
     * @return true - получили,  false - нет
     */
    private boolean isBookListReceived(){
        return booksList != null && booksList.size() > 0;
    }

    public void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    /**
     * В отдельном потоке парсим из заголовка ответа имя передаваемого файла и скачиваем его
     */
    class DownloadAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://itense.group/download?id=101";
            String response = HttpRequest.get(url).header("Content-Disposition");
            String fileName = response.substring(response.indexOf("=") + 1, response.length());

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading file, please wait...");
            request.setTitle("Downloading file");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            return null;
        }

        @Override
        protected void onPreExecute() {
            Snackbar.make(mCoordinatorLayout, R.string.isDownloading, Snackbar.LENGTH_LONG).show();
        }
    }
}
