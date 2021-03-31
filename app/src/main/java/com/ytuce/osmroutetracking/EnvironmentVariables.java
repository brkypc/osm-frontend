package com.ytuce.osmroutetracking;

public class EnvironmentVariables {

    private static final String ENVIRONMENT_KEREM = "kerem";
    private static final String ENVIRONMENT_BERKAY = "berkay";
    private static final String ENVIRONMENT_PRODUCTION = "prod";

    public static String MAPSERVER_LOCALHOST;
    public static String MAP_FILE_DIRECTORY;
    public static String PGSQL_LOCALHOST;
    public static String PGSQL_USERNAME;
    public static String PGSQL_PASSWORD;

    // change currentEnv after every `git pull`
    private static final String currentEnvironment = ENVIRONMENT_KEREM;

    // set variables at every startup (MainActivity.onCreate)
    public static void set() {
        switch (currentEnvironment) {
            case ENVIRONMENT_KEREM:
                MAPSERVER_LOCALHOST = "http://10.0.2.2";
                MAP_FILE_DIRECTORY = "D:/ms4w/apps/osm/basemaps/osm-google.map";
                PGSQL_LOCALHOST = ".";
                PGSQL_USERNAME = "postgres";
                PGSQL_PASSWORD = "1234";
                break;
            case ENVIRONMENT_BERKAY:
                MAPSERVER_LOCALHOST = ".";
                MAP_FILE_DIRECTORY = ".";
                PGSQL_LOCALHOST = ".";
                PGSQL_USERNAME = ".";
                PGSQL_PASSWORD = ".";
            case ENVIRONMENT_PRODUCTION:
            default:
                MAPSERVER_LOCALHOST = "..";
                MAP_FILE_DIRECTORY = ".";
                PGSQL_LOCALHOST = ".";
                PGSQL_USERNAME = ".";
                PGSQL_PASSWORD = ".";
        }
    }

}
