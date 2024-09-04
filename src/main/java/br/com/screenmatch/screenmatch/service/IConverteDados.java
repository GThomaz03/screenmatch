package br.com.screenmatch.screenmatch.service;

public interface IConverteDados {
    <T> T obterDados(String Json, Class<T> classe);
}
