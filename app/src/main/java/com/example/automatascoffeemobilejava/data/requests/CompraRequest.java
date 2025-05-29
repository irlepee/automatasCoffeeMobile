package com.example.automatascoffeemobilejava.data.requests;

public class CompraRequest {
    int id;
    int pedido;

    public CompraRequest(int id_repartidor, int id_pedido) {
        this.id = id_repartidor;
        this.pedido = id_pedido;
    }

    public int getIdRepartidor() {
        return id;
    }

    public int getIdPedido() {
        return pedido;
    }
}
