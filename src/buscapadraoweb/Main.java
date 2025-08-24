/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package buscapadraoweb;

import buscaweb.CapturaRecursosWeb;
import java.util.ArrayList;

/**
 *
 * @author Santiago
 */
public class Main {

    // busca char em vetor e retorna indice
    public static int get_char_ref (char[] vet, char ref ){
        for (int i=0; i<vet.length; i++ ){
            if (vet[i] == ref){
                return i;
            }
        }
        return -1;
    }

    // busca string em vetor e retorna indice
    public static int get_string_ref (String[] vet, String ref ){
        for (int i=0; i<vet.length; i++ ){
            if (vet[i].equals(ref)){
                return i;
            }
        }
        return -1;
    }

    //retorna o próximo estado, dado o estado atual e o símbolo lido
    public static int proximo_estado(char[] alfabeto, int[][] matriz,int estado_atual,char simbolo){
        int simbol_indice = get_char_ref(alfabeto, simbolo);
        if (simbol_indice != -1){
            return matriz[estado_atual][simbol_indice];
        }else{
            return -1;
        }
    }

    /// Função pra transição com letras.
    static void adicionarTransicaoLetras(int[][] matriz, String origem, String destino, String[] estados, char[] alfabeto) { 
        int from = get_string_ref(estados, origem);
        int to = get_string_ref(estados, destino);
        for (char c = 'A'; c <= 'Z'; c++) {
            matriz[from][get_char_ref(alfabeto, c)] = to;
        }
    }

    /// Função pra transição com numeros.
    static void adicionarTransicaoNumeros(int[][] matriz, String origem, String destino, String[] estados, char[] alfabeto) { 
        int from = get_string_ref(estados, origem);
        int to = get_string_ref(estados, destino);
        for (char c = '0'; c <= '9'; c++) {
            matriz[from][get_char_ref(alfabeto, c)] = to;
        }
    }

    /// Função pra transição final.
    static void adicionarTransicaoFinal(int[][] matriz, String origem, String[] estados, char[] alfabeto) { 
        int from = get_string_ref(estados, origem);
        for (char c = '0'; c <= '9'; c++) {
            matriz[from][get_char_ref(alfabeto, c)] = -1;
        }
    }

    /// Função para transições.
    public static int[][] construirAFD(String[] estados, char[] alfabeto) { 
        int[][] matriz = new int[8][36];

        adicionarTransicaoLetras(matriz, "q0", "q1", estados, alfabeto);
        adicionarTransicaoLetras(matriz, "q1", "q2", estados, alfabeto);
        adicionarTransicaoLetras(matriz, "q2", "q3", estados, alfabeto);
        adicionarTransicaoNumeros(matriz, "q3", "q4", estados, alfabeto);
        adicionarTransicaoLetras(matriz, "q4", "q5", estados, alfabeto);
        adicionarTransicaoNumeros(matriz, "q5", "q6", estados, alfabeto);
        adicionarTransicaoNumeros(matriz, "q6", "q7", estados, alfabeto);
        adicionarTransicaoFinal(matriz, "q7", estados, alfabeto);

        return matriz;
    }

    /*
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //instancia e usa objeto que captura código-fonte de páginas Web
        CapturaRecursosWeb crw = new CapturaRecursosWeb();
        crw.getListaRecursos().add("https://portalservicos.senatran.serpro.gov.br/#/veiculos/consultar/detalhes/12312312300/BBB2C33/01010101010");
        ArrayList<String> listaCodigos = crw.carregarRecursos();

        // String codigoHTML = listaCodigos.get(0);
        String codigoHTML = "https://servicos.detran.sc.gov.br/consulta-dossie-veiculo?placa=AAA1A11&renavam=00000000000";
        
        //mapa do alfabeto
        char[] alfabeto = new char[36];

        /// Preenche alfabeto pela tabela ascii 
        for (int i = 0; i < 10; i++){
            alfabeto[i] = (char) ('0' + i);
        }
        for (int i = 0; i < 26; i++) {
            alfabeto[10 + i] = (char) ('A' + i);
        }

        /// estados
        String[] estados = {"q0", "q1", "q2", "q3", "q4", "q5", "q6", "q7"};

        /// estado inicial
        String estado_inicial = "q0";

        //estados finais
        String[] estados_finais = new String[1];
        estados_finais[0] = "q7";
        
        /// cria a matriz 
        int[][] matriz = construirAFD(estados, alfabeto);
        
        int estado = get_string_ref (estados, estado_inicial);
        int estado_anterior = -1;
        ArrayList<String> palavras_reconhecidas = new ArrayList();

        String palavra = "";

        //varre o código-fonte de um código
        for (int i=0; i<codigoHTML.length(); i++){

            estado_anterior = estado;
            estado = proximo_estado(alfabeto, matriz, estado, codigoHTML.charAt(i));
            //se o não há transição
            if (estado == -1){
                //pega estado inicial
                estado = get_string_ref(estados, estado_inicial);
                // se o estado anterior foi um estado final
                if (get_string_ref(estados_finais, estados[estado_anterior]) != -1){
                    //se a palavra não é vazia adiciona palavra reconhecida
                    if ( ! palavra.equals("")){
                        palavras_reconhecidas.add(palavra);
                    }
                    // se ao analisar este caracter não houve transição
                    // teste-o novamente, considerando que o estado seja inicial
                    i--;
                }
                //zera palavra
                palavra = "";
                
            }else{
                //se houver transição válida, adiciona caracter a palavra
                palavra += codigoHTML.charAt(i);
            }
        }


        //foreach no Java para exibir todas as palavras reconhecidas
        for (String p: palavras_reconhecidas){
            System.out.println (p);
        }


    }



}
