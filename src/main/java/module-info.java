module com.example.clientservererecepta {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires jdk.httpserver;
    exports com.example.clientservererecepta.Client;
    exports com.example.clientservererecepta.Server;
    exports com.example.clientservererecepta.Server.Requests;
    exports com.example.clientservererecepta.Server.Util;
}
