package com.grinder.net.security.vpn;

import com.google.gson.annotations.SerializedName;

/*
 *
 * @author HiddenMotives
 */

public class Response {
    public String status;
    public String msg;

    @SerializedName("package")
    public String getPackage;

    public String remaining_requests;
    public String ipaddress;

    @SerializedName("host-ip")
    public boolean hostip;

    public String hostname;
    public String org;

    public CS country;
    public CS subdivision;

    public String city;
    public String postal;

    public latlon location;

    public static class CS {
        public String name;
        public String code;
    }

    public static class latlon {
        public double lat;

        @SerializedName("long")
        public double lon;
    }

}