package edu.example.bookcatalog.data;


import android.os.Parcel;
import android.os.Parcelable;

public class BookModel implements Parcelable{
    private String title;
    private String content;
    private String author;
    private String link;

    public BookModel(String title, String content, String author, String link) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.link = link;
    }

    protected BookModel(Parcel in) {
        title = in.readString();
        content = in.readString();
        author = in.readString();
        link = in.readString();
    }

    public static final Creator<BookModel> CREATOR = new Creator<BookModel>() {
        @Override
        public BookModel createFromParcel(Parcel in) {
            return new BookModel(in);
        }

        @Override
        public BookModel[] newArray(int size) {
            return new BookModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(author);
        dest.writeString(link);
    }
}
