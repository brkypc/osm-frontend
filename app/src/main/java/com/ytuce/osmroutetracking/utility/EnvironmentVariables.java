package com.ytuce.osmroutetracking.utility;

public class EnvironmentVariables {

    private static final String ENVIRONMENT_KEREM = "kerem";
    private static final String ENVIRONMENT_BERKAY = "berkay";
    private static final String ENVIRONMENT_PRODUCTION = "prod";

    public static String MAPSERVER_LOCALHOST;
    public static String MAP_FILE_DIRECTORY;
    public static String CLIENT_FILTER_MAP_FILE_DIRECTORY;
    public static String TRACKING_FILTER_MAP_FILE_DIRECTORY;
    public static String PGSQL_LOCALHOST;
    public static String PGSQL_USERNAME;
    public static String PGSQL_PASSWORD;
    public static String REST_API_BASE_URL;

    // change currentEnv after every `git pull`
    private static final String currentEnvironment = ENVIRONMENT_KEREM;

    // set variables at every startup (MainActivity.onCreate)
    public static void set() {
        switch (currentEnvironment) {
            case ENVIRONMENT_KEREM:
                MAPSERVER_LOCALHOST = "http://10.0.2.2";
                MAP_FILE_DIRECTORY = "D:/ms4w/apps/osm/basemaps/osm-google.map";
                CLIENT_FILTER_MAP_FILE_DIRECTORY = "D:/ms4w/apps/osm/basemaps/osm-google-filter-client.map";
                TRACKING_FILTER_MAP_FILE_DIRECTORY = "D:/ms4w/apps/osm/basemaps/osm-google-filter-tracking.map";
                PGSQL_LOCALHOST = ".";
                PGSQL_USERNAME = "postgres";
                PGSQL_PASSWORD = "1234";
                REST_API_BASE_URL = "http://10.0.2.2:8080/";
                break;
            case ENVIRONMENT_BERKAY:
                MAPSERVER_LOCALHOST = ".";
                MAP_FILE_DIRECTORY = ".";
                CLIENT_FILTER_MAP_FILE_DIRECTORY = ".";
                TRACKING_FILTER_MAP_FILE_DIRECTORY = ".";
                PGSQL_LOCALHOST = ".";
                PGSQL_USERNAME = ".";
                PGSQL_PASSWORD = ".";
                REST_API_BASE_URL = ".";
            case ENVIRONMENT_PRODUCTION:
            default:
                MAPSERVER_LOCALHOST = "..";
                MAP_FILE_DIRECTORY = ".";
                CLIENT_FILTER_MAP_FILE_DIRECTORY = ".";
                TRACKING_FILTER_MAP_FILE_DIRECTORY = ".";
                PGSQL_LOCALHOST = ".";
                PGSQL_USERNAME = ".";
                PGSQL_PASSWORD = ".";
                REST_API_BASE_URL = ".";
        }
    }

}
