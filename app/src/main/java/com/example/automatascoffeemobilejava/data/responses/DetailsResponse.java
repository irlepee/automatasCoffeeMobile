package com.example.automatascoffeemobilejava.data.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class DetailsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("purchase")
    private Purchase purchase;

    @SerializedName("details")
    private List<Detail> details;

    public String getStatus() {
        return status;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public static class Purchase {
        @SerializedName("id")
        private int id;

        @SerializedName("id_usuario")
        private int idUsuario;

        @SerializedName("id_repartidor")
        private int idRepartidor;

        @SerializedName("fecha")
        private String fecha;

        @SerializedName("entregado")
        private String entregado;

        @SerializedName("id_sucursal")
        private String idSucursal;

        @SerializedName("id_direccion")
        private int idDireccion;

        @SerializedName("latitud")
        private String latitud;

        @SerializedName("longitud")
        private String longitud;

        @SerializedName("id_tarjeta")
        private int idTarjeta;

        @SerializedName("total")
        private String total;

        @SerializedName("identificador")
        private String identificador;

        @SerializedName("estatus")
        private String estatus;

        @SerializedName("usuario")
        private String usuario;

        @SerializedName("repartidor")
        private String repartidor;

        @SerializedName("pago")
        private String pago;

        @SerializedName("detalle")
        private List<Object> detalle;

        public int getId() {
            return id;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public int getIdRepartidor() {
            return idRepartidor;
        }

        public String getFecha() {
            return fecha;
        }

        public String getEntregado() {
            return entregado;
        }

        public String getIdSucursal() {
            return idSucursal;
        }

        public int getIdDireccion() {
            return idDireccion;
        }

        public String getLatitud() {
            return latitud;
        }

        public String getLongitud() {
            return longitud;
        }

        public int getIdTarjeta() {
            return idTarjeta;
        }

        public String getTotal() {
            return total;
        }

        public String getIdentificador() {
            return identificador;
        }

        public String getEstatus() {
            return estatus;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getRepartidor() {
            return repartidor;
        }

        public String getPago() {
            return pago;
        }

        public List<Object> getDetalle() {
            return detalle;
        }
    }

    public static class Detail {
        @SerializedName("id_detalle_compra")
        private int idDetalleCompra;

        @SerializedName("id_compra")
        private int idCompra;

        @SerializedName("id_producto")
        private int idProducto;

        @SerializedName("id_tamaño")
        private int idTamaño;

        @SerializedName("cantidad")
        private int cantidad;

        @SerializedName("precio_unitario")
        private String precioUnitario;

        @SerializedName("subtotal")
        private String subtotal;

        @SerializedName("producto")
        private Producto producto;

        @SerializedName("tamaño")
        private Tamano tamaño;

        public int getIdDetalleCompra() {
            return idDetalleCompra;
        }

        public int getIdCompra() {
            return idCompra;
        }

        public int getIdProducto() {
            return idProducto;
        }

        public int getIdTamaño() {
            return idTamaño;
        }

        public int getCantidad() {
            return cantidad;
        }

        public String getPrecioUnitario() {
            return precioUnitario;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public Producto getProducto() {
            return producto;
        }

        public Tamano getTamaño() {
            return tamaño;
        }
    }

    public static class Producto {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("ruta")
        private String ruta;

        @SerializedName("descripcion")
        private String descripcion;

        @SerializedName("estatus")
        private int estatus;

        @SerializedName("categorias")
        private List<Integer> categorias;

        @SerializedName("tamanos")
        private List<Integer> tamanos;

        @SerializedName("precios")
        private Map<String, String> precios;

        @SerializedName("stocks")
        private Map<String, Integer> stocks;

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getRuta() {
            return ruta;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public int getEstatus() {
            return estatus;
        }

        public List<Integer> getCategorias() {
            return categorias;
        }

        public List<Integer> getTamanos() {
            return tamanos;
        }

        public Map<String, String> getPrecios() {
            return precios;
        }

        public Map<String, Integer> getStocks() {
            return stocks;
        }
    }

    public static class Tamano {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }
}