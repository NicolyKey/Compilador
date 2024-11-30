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
    private String operadorRelacionalAtual;
    private Map<String, Simbolo> tabelaSimbolos = new HashMap<>();
    private int contadorRotulos = 0;
    private Token token;

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
                acao117(token);
            case 118 ->
                acao118();
            case 119 ->
                acao119();
            case 120 ->
                acao120(token);
            case 121 ->
                acao121(token);
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
        codigoObjeto.add("ret");
        codigoObjeto.add("} }");
    }

    private void acao102(Token token) throws SemanticError {
        for (String id : listaId) {
            if (tabelaSimbolos.containsKey(id)) {
                throw new SemanticError(token.getLexeme() + " já declarado", token.getPosition());
            }

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
                    throw new SemanticError("Identificador '" + id + "' deve começar com i_, f_, s_ ou b_", token.getPosition());
            };
            tabelaSimbolos.put(id, new Simbolo(id, tipoIL, false, token.getPosition()));
            codigoObjeto.add(".locals (" + tipoIL + " " + id + ")");
        }
        listaId.clear();
    }

    private void acao103() throws SemanticError {
        String tipo = pilhaTipos.pop();

        if (tipo.equals("int64")) {
            codigoObjeto.add("conv.i8");
        }

        for (int i = 0; i < listaId.size() - 1; i++) {
            codigoObjeto.add("dup");
        }

        for (int i = 0; i < listaId.size(); i++) {
            String id = listaId.get(i);

            if (!tabelaSimbolos.containsKey(id)) {
                throw new SemanticError(token.getLexeme() + " não declarado", token.getPosition());
            }

            codigoObjeto.add("stloc " + id);
        }
        listaId.clear();
    }

    private void acao104(Token token) throws SemanticError {
        String id = token.getLexeme();
        listaId.add(id);
    }

    private void acao105(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

        if (!tabelaSimbolos.containsKey(lexeme)) {
            throw new SemanticError(lexeme + " não declarado", token.getPosition());
        }

        codigoObjeto.add("call string [mscorlib]System.Console::ReadLine()");

        String tipoIL = switch (lexeme.substring(0, 2)) {
            case "i_" -> {
                yield "call int64 [mscorlib]System.Int64::Parse(string)";
            }
            case "f_" -> {
                yield "call float64 [mscorlib]System.Double::Parse(string)";
            }
            case "s_" -> {
                yield "";
            }
            case "b_" -> {
                yield "call bool [mscorlib]System.Boolean::Parse(string)";
            }
            default ->
                throw new SemanticError("Tipo não suportado para leitura: " + lexeme, token.getPosition());
        };
        if (!tipoIL.isEmpty()) {
            codigoObjeto.add(tipoIL);
        }

        codigoObjeto.add("stloc " + lexeme);
    }

    private void acao106(Token token) throws SemanticError {
        String lexeme = token.getLexeme();
        codigoObjeto.add("ldstr " + lexeme);
        codigoObjeto.add("call void [mscorlib]System.Console::Write(string)");
    }

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
            codigoObjeto.add("call void [mscorlib]System.Console::WriteLine()");
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
        // Desempilha o tipo da pilha de tipos
        String tipo = pilhaTipos.pop();

        // Caso o tipo seja int64, converta para float64 antes da saída (em IL, int64 é tratado como float64)
        if (tipo.equals("int64")) {
            codigoObjeto.add("conv.i8"); // Converte para int64 (caso necessário)
        }

        // Gera o código objeto para escrever o valor com base no tipo desempilhado
        switch (tipo) {
            case "int64":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(int64)");
                break;
            case "float64":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(float64)");
                break;
            case "string":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(string)");
                break;
            case "bool":
                codigoObjeto.add("call void [mscorlib]System.Console::Write(bool)");
                break;
            default:
                throw new SemanticError("Tipo inválido para operação de escrita: " + tipo);
        }
    }

    private void acao109() throws SemanticError {
        String novoRotulo1 = novoRotulo();
        pilhaRotulos.push(novoRotulo1);

        String novoRotulo2 = novoRotulo();
        codigoObjeto.add(String.format("brfalse %s\n", novoRotulo2));

        pilhaRotulos.push(novoRotulo2);
    }

    private void acao110() throws SemanticError {
        String rotuloDesempilhado2 = pilhaRotulos.pop();
        String rotuloDesempilhado1 = pilhaRotulos.pop();

        codigoObjeto.add(String.format("br %s", rotuloDesempilhado1));
        pilhaRotulos.push(rotuloDesempilhado1);
        codigoObjeto.add(String.format("%s:", rotuloDesempilhado2));
    }

    private void acao111() throws SemanticError {
        String rotuloDesempilhado = pilhaRotulos.pop();
        codigoObjeto.add(String.format("%s:", rotuloDesempilhado));
    }

    private void acao112() throws SemanticError {
        String novoRotulo = novoRotulo();
        codigoObjeto.add(String.format("brfalse %s", novoRotulo));
        pilhaRotulos.push(novoRotulo);
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
        String rotuloDesempilhado = pilhaRotulos.pop();
        codigoObjeto.add(String.format("brfalse %s", rotuloDesempilhado));
    }

    private void acao116(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "&&");
        if (tipoResultante == null) {
            throw new SemanticError("Tipos incompatíveis para a operação '&&'", token.getPosition());
        }
        pilhaTipos.push(tipoResultante);
        codigoObjeto.add("and");
    }

    private void acao117(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "||");
        if (tipoResultante == null) {
            throw new SemanticError("Tipos incompatíveis para a operação '||'", token.getPosition());
        }

        pilhaTipos.push(tipoResultante);
        codigoObjeto.add("or");
    }

    private void acao118() {
        pilhaTipos.push("bool");
        codigoObjeto.add("ldc.i8 1\n");
    }

    private void acao119() throws SemanticError {
        pilhaTipos.push("bool");
        codigoObjeto.add("ldc.i8 0\n");
    }

    private void acao120(Token token) {
        codigoObjeto.add("ldc.i8 1\nxor\n");
    }

    private void acao121(Token token) throws SemanticError {
        operadorRelacionalAtual = token.getLexeme();
    }

    private void acao122() throws SemanticError {
        String tipo2 = pilhaTipos.pop(); // Tipo do lado direito
        String tipo1 = pilhaTipos.pop(); // Tipo do lado esquerdo

        if (!(tipo1.equals("int64") || tipo1.equals("float64")) || !(tipo2.equals("int64") || tipo2.equals("float64"))) {
            throw new SemanticError("Operador relacional inválido para os tipos: " + tipo1 + " e " + tipo2);
        }

        pilhaTipos.push("bool");

        switch (operadorRelacionalAtual) {
            case "==":
                codigoObjeto.add("ceq\n");
                break;
            case "!=":
                codigoObjeto.add("ceq\n");
                codigoObjeto.add("ldc.i8 1\nxor \n");
                break;
            case "<":
                codigoObjeto.add("clt\n");
                break;
            case ">":
                codigoObjeto.add("cgt\n");
                break;
            default:
                throw new SemanticError("Operador relacional desconhecido: " + operadorRelacionalAtual);
        }
    }

    private void acao123() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();
        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "+");

        if (tipoResultante == null) {
            throw new SemanticError("Tipos incompatíveis para a operação '+'");
        }

        codigoObjeto.add("add");
        pilhaTipos.push(tipoResultante);
    }

    private void acao124() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "-");
        if (tipoResultante == null) {
            throw new SemanticError("Tipos incompatíveis para a operação '-'");
        }

        codigoObjeto.add("sub");
        pilhaTipos.push(tipoResultante);
    }

    private void acao125() throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        String tipoResultante = verificarTipoResultado(tipo1, tipo2, "*");

        if ("int64".equals(tipoResultante) || "float64".equals(tipoResultante)) {
            codigoObjeto.add("mul");
        } else {
            throw new SemanticError("Operação inválida entre tipos: " + tipo1 + " e " + tipo2);
        }

        pilhaTipos.push(tipoResultante);
    }

    private void acao126(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();

        if (!tipo1.equals(tipo2)) {
            throw new SemanticError("Tipos incompatíveis para a operação de divisão. Ambos os operandos devem ser do mesmo tipo.", token.getPosition());
        }

        if (!tipo1.equals("int64") && !tipo1.equals("float64")) {
            throw new SemanticError("Operação de divisão suportada apenas para tipos numéricos (int64 ou float64).", token.getPosition());
        }

        codigoObjeto.add("div");
        pilhaTipos.push(tipo1);
    }

    private String pegarTipoId(String id) throws SemanticError {
        String prefixo = id.split("_")[0];
        switch (prefixo) {
            case "i":
                return "int64";
            case "f":
                return "float64";
            case "s":
                return "string";
            case "b":
                return "bool";
            default:
                throw new SemanticError("Identificador de variável incorreto: " + id, token.getPosition());
        }
    }

    private void acao127(Token token) throws SemanticError {
        String lexeme = token.getLexeme();

        if (!tabelaSimbolos.containsKey(lexeme)) {
            throw new SemanticError(lexeme + " não declarado", token.getPosition());
        }
        String varType = pegarTipoId(lexeme);
        pilhaTipos.push(varType);

        codigoObjeto.add("ldloc " + lexeme);

        if ("int64".equals(varType)) {
            codigoObjeto.add("conv.r8");
        }
    }

    private void acao128(Token token) {
        pilhaTipos.push("int64");
        codigoObjeto.add("dc.i8 %s\\nconv.r8\\n" + token.getLexeme());
    }

    private void acao129(Token token) {
        pilhaTipos.push("float64");
        String valor = token.getLexeme().replace(",", ".");
        codigoObjeto.add("ldc.r8 %s\n" + valor);
    }

    private void acao130() {
        pilhaTipos.push("string");
        codigoObjeto.add("ldstr %s\n" + token.getLexeme());
    }

    private String novoRotulo() {
        return "rotulo" + (contadorRotulos++);
    }

    private String verificarTipoResultado(String operando1, String operando2, String operacao) throws SemanticError {
        // Operações aritméticas
        if ("+".equals(operacao) || "-".equals(operacao) || "*".equals(operacao)) {
            if ("int64".equals(operando1) && "int64".equals(operando2)) {
                return "int64";
            } else if (("int64".equals(operando1) && "float64".equals(operando2))
                    || ("float64".equals(operando1) && "int64".equals(operando2))
                    || ("float64".equals(operando1) && "float64".equals(operando2))) {
                return "float64";
            }
        } else if ("/".equals(operacao)) {
            // Divisão exige que os tipos sejam iguais
            if ("int64".equals(operando1) && "int64".equals(operando2)) {
                return "int64";
            } else if ("float64".equals(operando1) && "float64".equals(operando2)) {
                return "float64";
            } else {
                throw new SemanticError("Divisão inválida entre tipos: " + operando1 + " e " + operando2);
            }
        } // Operações relacionais
        else if ("<".equals(operacao) || "<=".equals(operacao) || ">".equals(operacao) || ">=".equals(operacao)
                || "==".equals(operacao) || "!=".equals(operacao)) {
            if (("int64".equals(operando1) || "float64".equals(operando1))
                    && ("int64".equals(operando2) || "float64".equals(operando2))) {
                return "bool";
            }
        }
        // Operações lógicas
//    else if ("&".equals(operacao) || "|".equals(operacao)) {
//        if ("bool".equals(operando1) && "bool".equals(operando2)) {
//            return "bool";
//        }
//    } 
        throw new SemanticError("Operação inválida ou tipos incompatíveis: " + operando1 + " e " + operando2 + " com operação " + operacao);
    }

}
