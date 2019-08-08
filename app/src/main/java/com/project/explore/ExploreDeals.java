package com.project.explore;

import android.os.Parcel;
import android.os.Parcelable;

public class ExploreDeals implements Parcelable {
    public static final Creator<ExploreDeals> CREATOR = new Creator<ExploreDeals>() {
        @Override
        public ExploreDeals createFromParcel(Parcel in) {
            return new ExploreDeals(in);
        }

        @Override
        public ExploreDeals[] newArray(int size) {
            return new ExploreDeals[size];
        }
    };
    private String title;
    private String price;
    private String description;
    private String imageUrl;
    private String id;

    public ExploreDeals() {
    }

    protected ExploreDeals(Parcel in) {
        title = in.readString();
        price = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        id = in.readString();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(id);
    }
}
