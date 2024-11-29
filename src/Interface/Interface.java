/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Interface.NumberedBorder;
import java.awt.Image;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Master
 */
public class Interface extends JFrame {

    private static final long serializacao = 1L;
    private JTextArea mensagem;
    private JLabel status;
    private boolean arqSalvo = false;
    private File arquivoAtual;
//    private List<String> codigoObjeto = new ArrayList();
    private Semantico semantico = new Semantico();

    private JTextArea editor;

    // Início do método de salvar
    private void salvarArquivo(File arquivo) {
        if (arquivo != null) {
            String nomeArquivo = arquivo.getName();
            if (!nomeArquivo.toLowerCase().endsWith(".txt")) {
                // Se o arquivo não termina com .txt, adiciona a extensão .txt
                nomeArquivo += ".txt";
                arquivo = new File(arquivo.getParent(), nomeArquivo);
            }
            try (FileWriter writer = new FileWriter(arquivo)) {
                writer.write(editor.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Interface.this, "Erro ao salvar o arquivo!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
            arqSalvo = true;
            arquivoAtual = arquivo;
        } else {
            JOptionPane.showMessageDialog(Interface.this, "Nenhum arquivo selecionado para salvar!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

private void salvarCodigoObjeto() {
    if (arquivoAtual == null) {
        mensagem.setText("Salve o arquivo antes de compilar para gerar o .il");
        return;
    }

    // Obter o nome e caminho do arquivo .il
    String nomeArquivo = arquivoAtual.getName();
    String nomeArquivoIL = nomeArquivo.substring(0, nomeArquivo.lastIndexOf('.')) + ".il";
    File arquivoIL = new File(arquivoAtual.getParent(), nomeArquivoIL);

    try (FileWriter writer = new FileWriter(arquivoIL)) {
        // Obter o código objeto do Semantico
        List<String> codigoObjeto = semantico.getCodigoObjeto();

        // Verificar se há conteúdo no código objeto
        if (codigoObjeto == null || codigoObjeto.isEmpty()) {
            mensagem.setText("Erro: Código objeto está vazio. Certifique-se de que o código foi gerado corretamente.");
            return;
        }

        // Usar StringBuilder para concatenar as linhas do código objeto
        StringBuilder sb = new StringBuilder();
        for (String linha : codigoObjeto) {
            sb.append(linha).append("\n");
        }

        // Escrever no arquivo
        writer.write(sb.toString());
        mensagem.append("\nArquivo .il gerado com sucesso: " + arquivoIL.getAbsolutePath());
    } catch (IOException e) {
        mensagem.setText("Erro ao salvar o arquivo .il: " + e.getMessage());
    }
}



    private void Compilar() {
    Lexico lexico = new Lexico();
    Sintatico sintatico = new Sintatico();
    semantico = new Semantico();  // Inicializar a variável semantico
    String text = editor.getText();
    lexico.setInput(text);

    try {
        // Tradução dirigida pela sintaxe
        sintatico.parse(lexico, semantico);
        mensagem.setText("Programa compilado com sucesso");

        // Salvar o código fonte original e o código objeto
        salvarArquivo(arquivoAtual);
        salvarCodigoObjeto();
    } catch (LexicalError e1) {
        mensagem.setText("Erro na linha " + e1.getLinhaToken(editor.getText()) + ": " + e1.getToken(editor.getText()) + " " + e1.getMessage());
    } catch (SyntaticError e2) {
        String tokenClassName = sintatico.getTokenClassName();
        String lexeme = sintatico.getToken();
        String mensagemErro = (tokenClassName.equals("string"))
                ? "Erro na linha " + e2.getLinhaToken(editor.getText()) + " - encontrado constante_string " + e2.getMessage()
                : "Erro na linha " + e2.getLinhaToken(editor.getText()) + " - encontrado " + lexeme + " " + e2.getMessage();
        mensagem.setText(mensagemErro);
    } catch (SemanticError e3) {
        mensagem.setText("Erro semântico: " + e3.getMessage());
    }
}


    public Interface() {
        // area que define que meu editor de texto vai possuir uma borda com numeros
        editor = new JTextArea();
        editor.setBorder(new NumberedBorder());

        setMinimumSize(new Dimension(900, 600));
        setTitle("Interface do compilador");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Barra de ferramentas
        JPanel barraFerramen = new JPanel(new GridLayout(0, 1));
        barraFerramen.setPreferredSize(new Dimension(900, 70));
        barraFerramen.setMaximumSize(new Dimension(900, 70));
        barraFerramen.setMinimumSize(new Dimension(600, 70));
        getContentPane().add(barraFerramen, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setMinimumSize(new Dimension(900, 30));
        buttonPanel.setPreferredSize(new Dimension(900, 30));
        buttonPanel.setMaximumSize(new Dimension(900, 30));
        barraFerramen.add(buttonPanel);

        JButton btnnovoctrl = new JButton("\r\nNovo [CTRL + N]");

        btnnovoctrl.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "novo");
        btnnovoctrl.getActionMap().put("novo", new AbstractAction() {
            /**
             *
             */
            private static final long serializacao = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Limpar o conteúdo do editor
                editor.setText("");

                // Limpar o conteúdo da área de mensagens
                mensagem.setText("");

                // Limpar o texto da barra de status
                status.setText("");

                arquivoAtual = null;
                arqSalvo = false;

            }
        });

        btnnovoctrl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fila.png")));
        btnnovoctrl.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnnovoctrl.setHorizontalTextPosition(SwingConstants.CENTER);
        btnnovoctrl.setBounds(10, 10, 100, 50);
        btnnovoctrl.setAlignmentY(0.0f);
        btnnovoctrl.setMargin(new Insets(0, 0, 0, 0));
        btnnovoctrl.setFont(new Font("SansSerif", Font.PLAIN, 9));
        btnnovoctrl.setPreferredSize(new Dimension(170, 48));
        btnnovoctrl.setMnemonic(KeyEvent.VK_N);
        btnnovoctrl.setActionCommand("Novo");
        btnnovoctrl.addActionListener(toolBarListener);
        buttonPanel.setLayout(null);
        buttonPanel.add(btnnovoctrl);

        JButton button2 = new JButton("Abrir [CTRL + O]");
        button2.setHorizontalTextPosition(SwingConstants.CENTER);
        button2.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Inicio do botão abrir
        button2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "abrir");
        button2.getActionMap().put("abrir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de texto (.txt)", "txt");
                fileChooser.setFileFilter(filter);

                // Mostra o explorador de arquivos para escolher um arq
                int result = fileChooser.showOpenDialog(Interface.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    // Verificar se o arquivo possui a extensão .txt
                    if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                        JOptionPane.showMessageDialog(Interface.this,
                                "Por favor, selecione um arquivo de texto (.txt).", "Formato Inválido",
                                JOptionPane.ERROR_MESSAGE);
                        return; // Sai da ação do botão e não executa o código abaixo
                    }

                    try {
                        // Ler o conteúdo do arquivo e carregar no editor
                        FileReader reader = new FileReader(selectedFile);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        editor.setText(stringBuilder.toString());

                        // Limpar a área de mensagens
                        mensagem.setText("");

                        // Atualizar a barra de status com o nome do arquivo aberto
                        status.setText("Arquivo aberto: " + selectedFile.getAbsolutePath());
                        arquivoAtual = selectedFile;
                        arqSalvo = true;
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Interface.this, "Erro ao abrir o arquivo.", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });
        button2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pasta.png")));
        button2.setBounds(110, 10, 100, 50);
        button2.setAlignmentY(0.0f);
        button2.setMargin(new Insets(0, 0, 0, 0));
        button2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button2.setPreferredSize(new Dimension(170, 48));
        button2.setMnemonic(KeyEvent.VK_O);
        button2.setActionCommand("Abrir");
        button2.addActionListener(toolBarListener);
        buttonPanel.add(button2);

        JButton button3 = new JButton("Salvar [CTRL + S]");

        button3.setVerticalTextPosition(SwingConstants.BOTTOM);
        button3.setHorizontalTextPosition(SwingConstants.CENTER);
        button3.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "salvar");
        button3.getActionMap().put("salvar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /// Verificar se o arquivo já foi salvo anteriormente ou é novo
                if (arqSalvo) {
                    // Se o arquivo já foi salvo anteriormente, apenas salve as alterações
                    salvarArquivo(arquivoAtual);
                } else {
                    // Se for um novo arquivo, permita que o usuário escolha o local e o nome do
                    // arquivo
                    JFileChooser fileChoose = new JFileChooser();
                    int resultado = fileChoose.showSaveDialog(Interface.this);

                    // Verificar se o usuário selecionou um local para salvar o arquivo
                    if (resultado == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChoose.getSelectedFile();
                        // Salvar o conteúdo do editor no arquivo selecionado
                        salvarArquivo(selectedFile);
                        // Atualizar a barra de status com o nome do arquivo salvo
                        status.setText("Arquivo salvo: " + selectedFile.getAbsolutePath());
                        // Atualizar a variável para indicar que o arquivo foi salvo
                        arqSalvo = true;
                    }
                }
                // Limpar a área de mensagens após salvar
                mensagem.setText("");
            }
        });
        button3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/salvar.png")));
        button3.setBounds(210, 10, 100, 50);
        button3.setMargin(new Insets(0, 0, 0, 0));
        button3.setAlignmentY(0.0f);
        button3.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button3.setPreferredSize(new Dimension(170, 48));
        button3.setMnemonic(KeyEvent.VK_S);
        button3.setActionCommand("Salvar");
        button3.addActionListener(toolBarListener);
        buttonPanel.add(button3);

        JButton button4 = new JButton("Copiar [CTRL + C]");
        button4.setHorizontalTextPosition(SwingConstants.CENTER);
        button4.setVerticalTextPosition(SwingConstants.BOTTOM);
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.copy();
            }
        });
        button4.setBounds(310, 10, 100, 50);
        // Botão Copiar
        button4.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copiar");
        button4.getActionMap().put("copiar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.copy();
            }
        });
        button4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/copiar.png")));
        button4.setAlignmentY(0.0f);
        button4.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button4.setMargin(new Insets(0, 0, 0, 0));
        button4.setPreferredSize(new Dimension(170, 48));
        button4.setMnemonic(KeyEvent.VK_S);
        button4.setActionCommand("Copiar");
        buttonPanel.add(button4);

        JButton button5 = new JButton("Colar [CTRL + V]");
        button5.setVerticalTextPosition(SwingConstants.BOTTOM);
        button5.setHorizontalTextPosition(SwingConstants.CENTER);
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.paste();
            }

        });
        button5.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "colar");
        button5.getActionMap().put("colar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.paste();
            }
        });
        button5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cola.png")));
        button5.setBounds(410, 10, 100, 50);
        button5.setMargin(new Insets(0, 0, 0, 0));
        button5.setAlignmentY(0.0f);
        button5.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button5.setPreferredSize(new Dimension(170, 48));
        button5.setMnemonic(KeyEvent.VK_S);
        button5.setActionCommand("Colar");
        buttonPanel.add(button5);

        JButton button6 = new JButton("Recortar [CTRL + X]");
        button6.setHorizontalTextPosition(SwingConstants.CENTER);
        button6.setVerticalTextPosition(SwingConstants.BOTTOM);
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.cut();
            }
        });
        button6.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), "recortar");
        button6.getActionMap().put("recortar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.cut();
            }
        });
        button6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/recorte.png")));
        button6.setBounds(510, 10, 100, 50);
        button6.setAlignmentY(0.0f);
        button6.setMargin(new Insets(0, 0, 0, 0));
        button6.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button6.setPreferredSize(new Dimension(170, 48));
        button6.setMnemonic(KeyEvent.VK_S);
        button6.setActionCommand("Recortar");
        buttonPanel.add(button6);

        JButton button7 = new JButton("Compilar [F7]");
        button7.setVerticalTextPosition(SwingConstants.BOTTOM);

        button7.setHorizontalTextPosition(SwingConstants.CENTER);
        button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Compilar();
            }
        });
        button7.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
                "compilar");
        button7.getActionMap().put("compilar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Compilar();

            }
        });

        button7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/verifica.png")));
        button7.setBounds(610, 10, 100, 50);
        button7.setAlignmentY(0.0f);
        button7.setMargin(new Insets(0, 0, 0, 0));
        button7.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button7.setPreferredSize(new Dimension(170, 48));
        button7.setMnemonic(KeyEvent.VK_S);
        button7.setActionCommand("Compilar");
        buttonPanel.add(button7);

        JButton button8 = new JButton("Equipe [F1]");
        button8.setVerticalTextPosition(SwingConstants.BOTTOM);

        button8.setHorizontalTextPosition(SwingConstants.CENTER);
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mensagem.setText("Equipe: Ana Carolina Hausmann e Nicoly Keyssiane Lima Araujo");
            }
        });
        button8.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "equipe");
        button8.getActionMap().put("equipe", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mensagem.setText("Equipe: Ana Carolina Hausmann e Nicoly Keyssiane Lima Araujo");
            }
        });
        button8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/equipe.png")));
        button8.setBounds(710, 10, 100, 50);
        button8.setAlignmentY(0.0f);
        button8.setMargin(new Insets(0, 0, 0, 0));
        button8.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button8.setPreferredSize(new Dimension(170, 48));
        button8.setMnemonic(KeyEvent.VK_S);
        button8.setActionCommand("Equipe");
        buttonPanel.add(button8);

        // Editor
        editor = new JTextArea();
        editor.setBorder(new NumberedBorder());
        JScrollPane editorScrollPane = new JScrollPane(editor);
        editorScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(600, 400));

        // Area para mensagens
        mensagem = new JTextArea();
        mensagem.setFont(new Font("Courier New", Font.PLAIN, 13));
        mensagem.setPreferredSize(new Dimension(700, 1000));
        mensagem.setMaximumSize(new Dimension(900, 1000));
        mensagem.setMinimumSize(new Dimension(900, 25));
        mensagem.setEditable(false);
        JScrollPane messagesScrollPane = new JScrollPane(mensagem);
        messagesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        messagesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        messagesScrollPane.setMaximumSize(new Dimension(900, 100));
        messagesScrollPane.setPreferredSize(new Dimension(500, 100));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScrollPane, messagesScrollPane);
        splitPane.setResizeWeight(0.7);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Barra de status
        status = new JLabel(" ");
        status.setMaximumSize(new Dimension(900, 25));
        status.setMinimumSize(new Dimension(900, 25));
        status.setPreferredSize(new Dimension(900, 25));
        getContentPane().add(status, BorderLayout.SOUTH);
    }
    private ActionListener toolBarListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {

                case "Novo":
                    // Limpar o conteúdo do editor
                    editor.setText("");

                    // Limpar o conteúdo da área de mensagens
                    mensagem.setText("");

                    // Limpar o texto da barra de status
                    status.setText("");

                    arquivoAtual = null;
                    arqSalvo = false;

                    break;

                case "Abrir":
                    // Criar um seletor de arquivo
                    JFileChooser fileChooser = new JFileChooser();

                    // Configurar o filtro para exibir apenas arquivos .txt
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de texto (.txt)", "txt");
                    fileChooser.setFileFilter(filter);

                    // Mostrar a caixa de diálogo para selecionar o arquivo
                    int result = fileChooser.showOpenDialog(Interface.this);

                    // Verificar se o usuário selecionou um arquivo
                    if (result == JFileChooser.APPROVE_OPTION) {
                        // Obter o arquivo selecionado
                        File selectedFile = fileChooser.getSelectedFile();

                        // Verificar se o arquivo possui a extensão .txt
                        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                            JOptionPane.showMessageDialog(Interface.this,
                                    "Por favor, selecione um arquivo de texto (.txt).", "Formato Inválido",
                                    JOptionPane.ERROR_MESSAGE);
                            return; // Sai da ação do botão e não executa o código abaixo
                        }

                        try {
                            // Ler o conteúdo do arquivo e carregar no editor
                            FileReader reader = new FileReader(selectedFile);
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            bufferedReader.close();
                            editor.setText(stringBuilder.toString());

                            // Limpar a área de mensagens
                            mensagem.setText("");

                            // Atualizar a barra de status com o nome do arquivo aberto
                            status.setText("Arquivo aberto: " + selectedFile.getAbsolutePath());
                            arquivoAtual = selectedFile;
                            arqSalvo = true;
                        } catch (IOException ex) {
                            // Tratar exceções de leitura do arquivo
                            JOptionPane.showMessageDialog(Interface.this, "Erro ao abrir o arquivo.", "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case "Salvar":
                    // Verificar se o arquivo já foi salvo anteriormente ou é novo
                    if (arqSalvo) {
                        // Se o arquivo já foi salvo anteriormente, apenas salve as alterações
                        salvarArquivo(arquivoAtual);
                    } else {
                        // Se for um novo arquivo, permita que o usuário escolha o local e o nome do
                        // arquivo
                        JFileChooser fileChoose = new JFileChooser();
                        int resultado = fileChoose.showSaveDialog(Interface.this);

                        // Verificar se o usuário selecionou um local para salvar o arquivo
                        if (resultado == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChoose.getSelectedFile();
                            // Salvar o conteúdo do editor no arquivo selecionado
                            salvarArquivo(selectedFile);
                            // Atualizar a barra de status com o nome do arquivo salvo
                            status.setText("Arquivo salvo: " + selectedFile.getAbsolutePath());
                            // Atualizar a variável para indicar que o arquivo foi salvo
                            arqSalvo = true;
                        }
                    }
                    // Limpar a área de mensagens após salvar
                    mensagem.setText("");
                    break;

                case "Copiar":
                    editor.copy();
                    break;
                case "Colar":
                    editor.paste();
                    break;
                case "Recortar":
                    editor.cut();
                    break;
                case "Compilar":

                    break;

                case "Equipe":
                    mensagem.setText("Equipe: Ana Carolina Hausmann e Nicoly Keyssiane Lima Araujo");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            Interface compilerInterface = new Interface();
            compilerInterface.setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   
}
