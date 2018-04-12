
package com.nikola.jakshic.dagger.model.match;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeroHealingPerMin {

    @SerializedName("raw")
    @Expose
    public double raw;
    @SerializedName("pct")
    @Expose
    public double pct;

}