package com.ebabu.tooreest.beans;

import com.google.gson.annotations.Expose;

/**
 * Created by hp on 17/02/2017.
 */
public class Subcategory {
    @Expose
    private String category_id;
    @Expose
    private String subcategory_id;
    private String subcategory_name;
    private String icon;
    @Expose
    private int fees = 100;
    private int projectdone;
    private float ave_rating;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public int getProjectdone() {
        return projectdone;
    }

    public void setProjectdone(int projectdone) {
        this.projectdone = projectdone;
    }

    public float getAve_rating() {
        return ave_rating;
    }

    public void setAve_rating(float ave_rating) {
        this.ave_rating = ave_rating;
    }

    @Override
    public boolean equals(Object obj) {
        Subcategory subcategory = (Subcategory) obj;
        if (subcategory.getSubcategory_id().equals(this.subcategory_id)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(subcategory_id);
    }

    @Override
    public String toString() {
        return subcategory_name;
    }
}
