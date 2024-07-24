package dev.makeev.logging_to_db_starter.service;

public interface LogService {
    void addLog(String loginArg, String methodName);
}