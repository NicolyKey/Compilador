package Interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {

    private List<String> codigoObjeto = new ArrayList<>();
    private List<String> listaId = new ArrayList<>();
    private String operadorRelacional = "";
    private Stack<String> pilhaTipos = new Stack<>();
    private Stack<String> pilhaRotulos = new Stack<>();
    private Map<String, Simbolo> tabelaSimbolos = new HashMap<>();
    private int contadorRotulos = 0;

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 100 ->
                acao100();
            case 101 ->
                acao101();
            case 102 ->
                acao102(token);
            case 103 ->
                acao103();
            case 104 ->
                acao104(token);
            case 105 ->
                acao105(token);
            case 106 ->
                acao106(token);
            case 107 ->
                acao107(token);
            case 108 ->
                acao108();
            case 109 ->
                acao109();
            case 110 ->
                acao110();
            case 111 ->
                acao111();
            case 112 ->
                acao112();
            case 113 ->
                acao113();
            case 114 ->
                acao114();
            case 115 ->
                acao115(token);
            case 116 ->
                acao116(token);
            case 117 ->
                acao117();
            case 118 ->
                acao118();
            case 119 ->
                acao119();
            case 120 ->
                acao120(token);
            case 121 ->
                acao121();
            case 122 ->
                acao122();
            case 123 ->
                acao123();
            case 124 ->
                acao124();
            case 125 ->
                acao125();
            case 126 ->
                acao126(token);
            case 127 ->
                acao127(token);
            case 128 ->
                acao128(token);
            case 129 ->
                acao129(token);
            case 130 ->
                acao130();
            default ->
                throw new SemanticError("Ação semântica desconhecida: " + action, token.getPosition());
        }
    }

    public List<String> getCodigoObjeto() {
        return codigoObjeto;
    }

    private void acao100() {
        codigoObjeto.add(".assembly extern mscorlib {}");
        codigoObjeto.add(".assembly _codigoObjeto {}");
        codigoObjeto.add(".module _codigoObjeto.exe");
        codigoObjeto.add(".class public _UNICA {");
        codigoObjeto.add(".method static public void _principal() {");
        codigoObjeto.add(".entrypoint");
    }

    private void acao101() throws SemanticError {
        // Finalizar código objeto
        codigoObjeto.add("ret");
        codigoObjeto.add("} }");

        // Verificar se há variáveis não utilizadas
        for (String id : tabelaSimbolos.keySet()) {
            Simbolo simbolo = tabelaSimbolos.get(id);
//            if (!simbolo.isUsado()) {
//                throw new SemanticError("Variável '" + id + "' declarada, mas não utilizada. ", simbolo.getLinha());
//            }
        }
    }

    private void acaoAtribuicao(String id) throws SemanticError {
        // Verificar se a variável foi declarada
        if (!tabelaSimbolos.containsKey(id)) {
            throw new SemanticError("Variável '" + id + "' não declarada.", 0); // Ajuste a posição conforme necessário
        }

        // Marcar a variável como usada
        tabelaSimbolos.get(id).setUsado(true);

        // Gerar código objeto para armazenar o valor na variável
        codigoObjeto.add("stloc " + id);
    }

    private void acao102(Token token) throws SemanticError {
        for (String id : listaId) {
            // Verificar se o identificador já está declarado na tabela de símbolos
            if (tabelaSimbolos.containsKey(id)) {
                throw new SemanticError("Identificador '" + id + "' já declarado.", token.getPosition());
            }

            // Determinar o tipo com base no prefixo do identificador
            String tipoIL = switch (id.substring(0, 2)) {
                case "i_" ->
                    "int64";
                case "f_" ->
                    "float64";
                case "s_" ->
                    "string";
                case "b_" ->
                    "bool";
                default ->
                    throw new SemanticError(
                            "Identificador '" + id + "' deve começar com i_, f_, s_ ou b_", token.getPosition());
            };

            // Inserir na tabela de símbolos
            tabelaSimbolos.put(id, new Simbolo(id, tipoIL, false, token.getPosition()));

            // Gerar o código IL para declarar o identificador
            codigoObjeto.add(".locals (" + tipoIL + " " + id + ")");
        }

        // Limpar a lista de identificadores após o processamento
        listaId.clear();
    }

    private void acao103() {
        listaId.clear();
    }

    private void acao104(Token token) throws SemanticError {
        String id = token.getLexeme();

        // Verificar prefixo e determinar tipo
        String tipo;
        if (id.startsWith("i_")) {
            tipo = "int64";
        } else if (id.startsWith("f_")) {
            tipo = "float64";
        } else if (id.startsWith("s_")) {
            tipo = "string";
        } else if (id.startsWith("b_")) {
            tipo = "bool";
        } else {
            throw new SemanticError("Identificador '" + id + "' deve começar com i_, f_, s_ ou b_", token.getPosition());
        }

        listaId.add(id);
    }
//modifica read

    private void acao105(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

        // Verificar se é uma constante string ou identificador
        if (lexeme.startsWith("\"") && lexeme.endsWith("\"")) {
            // Constante string: Gerar código para exibição
            codigoObjeto.add("ldstr " + lexeme); // Instrução IL para carregar uma string
            codigoObjeto.add("call void [mscorlib]System.Console::Write(string)");
        } else {
            // É um identificador, verificar se está declarado
            if (!tabelaSimbolos.containsKey(lexeme)) {
                throw new SemanticError("Identificador '" + lexeme + "' não declarado.", token.getPosition());
            }

            // Gerar código para leitura com base no tipo do identificador
            String tipoIL = switch (lexeme.substring(0, 2)) {
                case "i_" ->
                    "call int64 [mscorlib]System.Int64::Parse(string)";
                case "f_" ->
                    "call float64 [mscorlib]System.Double::Parse(string)";
                case "s_" ->
                    ""; // Strings não precisam de conversão
                case "b_" ->
                    "call bool [mscorlib]System.Boolean::Parse(string)";
                default ->
                    throw new SemanticError("Tipo não suportado para leitura: " + lexeme, token.getPosition());
            };

            // Sempre chamar ReadLine() primeiro
            codigoObjeto.add("call string [mscorlib]System.Console::ReadLine()");

            // Adicionar conversão se necessário
            if (!tipoIL.isEmpty()) {
                codigoObjeto.add(tipoIL);
            }

            // Armazenar valor lido no identificador
            codigoObjeto.add("stloc " + lexeme);

            // Marcar o identificador como usado
            tabelaSimbolos.get(lexeme).setUsado(true);
        }
    }

    //ação write
    private void acao106(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

        // Verificar se o lexema é uma constante string
        if (lexeme.startsWith("\"") && lexeme.endsWith("\"")) {
            // Gerar código para carregar e escrever a constante string
            codigoObjeto.add("ldstr " + lexeme); // Instrução IL para carregar a string
            codigoObjeto.add("call void [mscorlib]System.Console::Write(string)");
        } else {
            // Caso contrário, tratar como identificador
            if (!tabelaSimbolos.containsKey(lexeme)) {
                throw new SemanticError("Identificador '" + lexeme + "' não declarado.", token.getPosition());
            }

            // Gerar código objeto para carregar o valor do identificador
            codigoObjeto.add("ldloc " + lexeme);

            // Determinar instrução de escrita com base no tipo do identificador
            String tipoIL = switch (lexeme.substring(0, 2)) {
                case "i_" -> {
                    codigoObjeto.add("conv.r8"); // Inteiros devem ser convertidos para float64
                    yield "call void [mscorlib]System.Console::Write(float64)";
                }
                case "f_" ->
                    "call void [mscorlib]System.Console::Write(float64)";
                case "s_" ->
                    "call void [mscorlib]System.Console::Write(string)";
                case "b_" ->
                    "call void [mscorlib]System.Console::Write(bool)";
                default ->
                    throw new SemanticError("Tipo não suportado para escrita: " + lexeme, token.getPosition());
            };

            // Adicionar instrução de escrita ao código objeto
            codigoObjeto.add(tipoIL);
        }
    }

// Adicione este método para verificar o tipo baseado no prefixo
    private String getTipoFromId(String id) throws SemanticError {
        if (id.startsWith("i_")) {
            return "int64";
        }
        if (id.startsWith("f_")) {
            return "float64";
        }
        if (id.startsWith("s_")) {
            return "string";
        }
        if (id.startsWith("b_")) {
            return "bool";
        }
        throw new SemanticError("Identificador com prefixo inválido: " + id);
    }

    private void acao107(Token token) throws SemanticError {
        for (String id : listaId) {
            if (!tabelaSimbolos.containsKey(id)) {
                throw new SemanticError("Variável '" + id + "' não declarada.", token.getPosition());
            }
            String tipo = tabelaSimbolos.get(id).getTipo();
            codigoObjeto.add("call string [mscorlib]System.Console::ReadLine()");
            if ("int64".equals(tipo)) {
                codigoObjeto.add("call int64 [mscorlib]System.Int64::Parse(string)");
            } else if ("float64".equals(tipo)) {
                codigoObjeto.add("call float64 [mscorlib]System.Double::Parse(string)");
            }
            codigoObjeto.add("stloc " + id);
            tabelaSimbolos.get(id).setUsado(true);
        }
        listaId.clear();
    }

    private void acao108() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Operação de adição requer operandos do mesmo tipo.");
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação de adição suportada apenas para tipos numéricos.");
        }

        // Gera o código da soma
        codigoObjeto.add("add");

        // Empilha o tipo resultante
        pilhaTipos.push(tipo1);
    }

    private void acao109() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Operação de subtração requer operandos do mesmo tipo.");
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação de subtração suportada apenas para tipos numéricos.");
        }

        // Gera o código da subtração
        codigoObjeto.add("sub");

        // Empilha o tipo resultante
        pilhaTipos.push(tipo1);
    }

    private void acao110() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Operação de multiplicação requer operandos do mesmo tipo.");
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação de multiplicação suportada apenas para tipos numéricos.");
        }

        // Gera o código da multiplicação
        codigoObjeto.add("mul");

        // Empilha o tipo resultante
        pilhaTipos.push(tipo1);
    }

    private void acao111() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Operação de divisão requer operandos do mesmo tipo.");
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação de divisão suportada apenas para tipos numéricos.");
        }

        // Gera o código da divisão
        codigoObjeto.add("div");

        // Empilha o tipo resultante
        pilhaTipos.push(tipo1);
    }

    private void acao112() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Operação '>' requer operandos do mesmo tipo.");
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação '>' suportada apenas para tipos numéricos.");
        }

        // Gera o código para comparação maior que
        codigoObjeto.add("cgt");

        // Empilha o tipo resultante
        pilhaTipos.push("bool");
    }

    private void acao113() {
        String novoRotulo = novoRotulo();
        codigoObjeto.add(novoRotulo + ":");
        pilhaRotulos.push(novoRotulo);
    }

    private void acao114() {
        String rotuloDesempilhado = pilhaRotulos.pop();
        codigoObjeto.add("brfalse " + rotuloDesempilhado);
    }

    private void acao115(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        // Lógica para determinar o tipo resultante conforme a TABELA DE TIPOS
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "&");

        if (tipoResultante == null) {
            throw new SemanticError("Tipos incompatíveis para a operação", token.getPosition());
        }

        // Empilhar o tipo resultante na pilha de tipos
        pilhaTipos.push(tipoResultante);
    }

    private void acao116(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        // Lógica para determinar o tipo resultante conforme a TABELA DE TIPOS
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "|");

        if (tipoResultante == null) {
            throw new SemanticError("Tipos incompatíveis para a operação", token.getPosition());
        }

        // Empilhar o tipo resultante na pilha de tipos
        pilhaTipos.push(tipoResultante);
    }

    private void acao117() {
        pilhaTipos.push("bool");
        codigoObjeto.add("ldc.i4.1");
    }

    private void acao118() {
        pilhaTipos.push("bool");
        codigoObjeto.add("ldc.i4.0");
    }

    private void acao119() throws SemanticError {
        String tipoExpressao = pilhaTipos.pop(); // Desempilha o tipo da expressão

        // Gera código objeto para negação lógica
        codigoObjeto.add("ldc.i4.0"); // Carrega o valor 0 na pilha
        codigoObjeto.add("ceq"); // Compara com zero (0) para inverter o valor booleano

        // Empilha o tipo resultante da negação, que é bool
        pilhaTipos.push("bool");
    }

    private void acao120(Token token) {
        operadorRelacional = token.getLexeme();
    }

    private void acao121() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        // Determina o tipo resultante da operação relacional
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "calcular");

        // Gera código objeto para a operação relacional em IL
        switch (operadorRelacional) {
            case "==":
                codigoObjeto.add("ceq");
                break;
            case "!=":
                codigoObjeto.add("ceq");
                codigoObjeto.add("ldc.i4.0");
                codigoObjeto.add("ceq");
                break;
            case "<":
                codigoObjeto.add("clt");
                break;
            case ">":
                codigoObjeto.add("cgt");
                break;

        }

        // Empilha o tipo resultante da operação
        pilhaTipos.push(tipoResultante);
    }

    private void acao122() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        // Verificar tipo resultante da operação
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "calcular");

        // Gerar código objeto em IL
        switch (tipoResultante) {
            case "int64":
                codigoObjeto.add("add");
                break;
            case "float64":
                codigoObjeto.add("add");
                break;
        }

        // Empilhar o tipo resultante na pilha de tipos
        pilhaTipos.push(tipoResultante);
    }

    private void acao123() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        // Verificar tipo resultante da operação
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "calcular");

        // Gerar código objeto em IL
        switch (tipoResultante) {
            case "int64":
                codigoObjeto.add("sub");
                break;
            case "float64":
                codigoObjeto.add("sub");
                break;
        }

        // Empilhar o tipo resultante na pilha de tipos
        pilhaTipos.push(tipoResultante);
    }

    private void acao124() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();
        // Verificar tipo resultante da operação
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "calcular");

        // Gerar código objeto em IL
        switch (tipoResultante) {
            case "int64":
                codigoObjeto.add("mul");
                break;
            case "float64":
                codigoObjeto.add("mul");
                break;
        }

        // Empilhar o tipo resultante na pilha de tipos
        pilhaTipos.push(tipoResultante);
    }

    private void acao125() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        // Verificar tipo resultante da operação
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "calcular");

        // Gerar código objeto em IL
        switch (tipoResultante) {
            case "int64":
                codigoObjeto.add("div");
                break;
            case "float64":
                codigoObjeto.add("div");
                break;

        }

        // Empilhar o tipo resultante na pilha de tipos
        pilhaTipos.push(tipoResultante);
    }

    private void acao126(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

        // Verificar se o identificador está na tabela de símbolos
        if (!tabelaSimbolos.containsKey(lexeme)) {
            throw new SemanticError(lexeme + " não declarado", token.getPosition());
        }

        // Obter o tipo do identificador da tabela de símbolos
        String tipo = tabelaSimbolos.get(lexeme).getTipo();

        // Gerar código objeto para carregar o valor do identificador
        codigoObjeto.add("ldloc " + lexeme);

        // Verificar se o tipo do identificador é int64 para conversão
        if (tipo.equals("int") || tipo.equals("int64")) {
            codigoObjeto.add("conv.r8"); // Converter para float64 (conv.r8)
            tipo = "int64"; // Atualizar tipo para float64 na pilha de tipos
        }
        if (tipo.equals("float")) {
            tipo = "float64"; // Atualizar tipo para float64 na pilha de tipos
        }
        // Empilhar o tipo do identificador na pilhaTipos
        pilhaTipos.push(tipo);
    }

    private void acao127(Token token) {
        // Empilhar na pilha_tipos o tipo correspondente, conforme TABELA DE TIPOS, ou
        // seja, int64
        pilhaTipos.push("int64");

        // Gerar código objeto para carregar o valor da constante
        codigoObjeto.add("ldc.i8 " + token.getLexeme());

        // Como cte_int é tratada como float64 em IL, converter para float64 (conv.r8)
        codigoObjeto.add("conv.r8");
    }

    private void acao128(Token token) {
        pilhaTipos.push("float64");
        codigoObjeto.add("ldc.r8 " + token.getLexeme());
    }

    private void acao129(Token token) {
        pilhaTipos.push("string");
        codigoObjeto.add("ldstr " + token.getLexeme());
    }

    private void acao130() {
        codigoObjeto.add("ldc.i8 -1"); // Carregar -1 na pilha como um inteiro de 64 bits
        codigoObjeto.add("conv.r8"); // Converter para float64 (se necessário)
        codigoObjeto.add("mul"); // Multiplicar pelo operando anterior na expressão
    }

    private String novoRotulo() {
        return "rotulo" + (contadorRotulos++);
    }

    class Simbolo {

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

    private String verificarTipoResultado(String operando1, String operando2, String operacao) {

        // Consultar a tabela de tipos para operações binárias
        if (operacao.equals("calcular")) {
            if (operando1.equals("int64") && operando2.equals("int64")) {
                return "int64";
            } else if ((operando1.equals("int64") && operando2.equals("float64"))
                    || (operando1.equals("float64") && operando2.equals("int64"))
                    || (operando1.equals("float64") && operando2.equals("float64"))) {
                return "float64";
            }
        } else if (operacao.equals("comparar")) {

            return "bool";

        } else if (operacao.equals("&") || operacao.equals("|")) {
            if (operando1.equals("bool") && operando2.equals("bool")) {
                return "bool";
            }
        }

        // Caso não seja possível determinar o tipo resultante, retorna null ou lança
        // uma exceção
        return null;
    }

}
