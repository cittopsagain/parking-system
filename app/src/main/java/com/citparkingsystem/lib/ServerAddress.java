package com.citparkingsystem.lib;

/**
 * Created by Walter Ybanez on 7/24/2017.
 */

public class ServerAddress {
    // Initialize your IP here, and PORT if you have

    // public static final String IP = "192.168.254.102";
    public static final String IP = "192.168.254.102:";
    public static final String PORT = "90";

    // Folder name of your PHP script that will handle all the requests from client
    public static final String PACKAGE = "cit_parking_system/";

    // Assemble now the path
    // public static final String URL = "http://"+IP+"/"+PACKAGE+"client/index.php";
    public static final String URL = "http://"+IP+PORT+"/"+PACKAGE+"client/index.php";
}
