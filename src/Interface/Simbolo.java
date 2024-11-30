/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interface;

/**
 *
 * @author nicol
 */
   public class Simbolo {

        private String id;
        private String tipo;
        private boolean usado;
        private int linha;
        
        
        public Simbolo(String id, String tipo, boolean usado, int linha) {
            this.id = id;
            this.tipo = tipo;
            this.usado = usado;
            this.linha = linha;
        }

        public String getId() {
            return id;
        }

        public String getTipo() {
            return tipo;
        }

        public boolean isUsado() {
            return usado;
        }

        public void setUsado(boolean usado) {
            this.usado = usado;
        }

        public int getLinha() {
            return linha;
        }
    }
