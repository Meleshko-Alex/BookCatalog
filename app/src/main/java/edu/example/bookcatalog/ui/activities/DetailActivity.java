package edu.example.bookcatalog.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.example.bookcatalog.R;
import edu.example.bookcatalog.data.BookModel;
import edu.example.bookcatalog.utils.ConstantManager;

public class DetailActivity extends Activity {
    private final String url = "http://epubbooks.ru";
    private BookModel mBookData;
    private TextView tv_title, tv_author, tv_description;
    private ImageView book_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBookData = getIntent().getParcelableExtra(ConstantManager.EXTRA_SELECTED_BOOK);

        book_image = (ImageView)findViewById(R.id.book_image);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_author = (TextView)findViewById(R.id.tv_author);
        tv_description = (TextView)findViewById(R.id.tv_description);

        tv_title.setText(mBookData.getTitle());
        tv_author.setText(mBookData.getAuthor());
        tv_description.setText(mBookData.getContent());

        Picasso.with(this)
                .load(url + mBookData.getLink())
                .placeholder(R.drawable.no_image)
                .into(book_image);

    }
}
