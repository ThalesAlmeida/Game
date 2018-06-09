package com.thale.jogo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Game extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] dragao;

    private Texture fundo;

    private Texture fogo4;
    private Texture fogo1;
    private Texture fogo3;
    private Texture fogo2;

    private Texture gameOver;

    private Random numeroRandomico;

    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle dragaoCirculo;
    private Rectangle retanguloFogo1;
    private Rectangle retanguloFogo2;
    private Rectangle retanguloFogo3;
    private Rectangle retanguloFogo4;
    //private ShapeRenderer shape;

    //Atributos de configuracao
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo=0;// 0-> jogo não iniciado 1-> jogo iniciado 2-> Game Over
    private int pontuacao=0;

    private float variacao = 0;
    private float velocidadeQueda=0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoFogoHorizontal;
    private float espacoEntreFogo;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto=false;

    //Câmera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

    @Override
    public void create () {

        batch = new SpriteBatch();
        numeroRandomico = new Random();
        dragaoCirculo = new Circle();
        retanguloFogo1 = new Rectangle();
        retanguloFogo3 = new Rectangle();
        retanguloFogo4 = new Rectangle();
        retanguloFogo2 = new Rectangle();
        //shape = new ShapeRenderer();
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);


        dragao = new Texture[4];
        dragao[0] = new Texture("dragao1.png");
        dragao[1] = new Texture("dragao2.png");
        dragao[2] = new Texture("dragao3.png");
        dragao[3] = new Texture("dragao4.png");


        fundo = new Texture("fundo.png");
        fogo1 = new Texture("fire1.png");
        fogo2 = new Texture("fire2.png");
        fogo3 = new Texture("fire3.png");
        fogo4 = new Texture("fire4.png");


        gameOver = new Texture("game_over.png");

        /**********************************************
         * Configuração da câmera
         * */
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo  = VIRTUAL_HEIGHT;

        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoFogoHorizontal = larguraDispositivo;
        espacoEntreFogo = 300;

    }

    @Override
    public void render () {

        camera.update();

        // Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10;
        if (variacao > 2) variacao = 0;

        if( estadoJogo == 0 ){//Não iniciado

            if( Gdx.input.justTouched() ){
                estadoJogo = 1;
            }

        }else {//Iniciado
            int contador = 0;
            if(pontuacao >= 3){
                posicaoMovimentoFogoHorizontal -= deltaTime * 600;
                contador ++;
            }

            if(contador == 2){
                posicaoMovimentoFogoHorizontal -=deltaTime *800;
            }


            velocidadeQueda++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

            if( estadoJogo == 1 ){//iniciado

                posicaoMovimentoFogoHorizontal -= deltaTime * 300;

                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }

                //Verifica se o fogo saiu inteiramente da tela
                if (posicaoMovimentoFogoHorizontal < -fogo2.getWidth()) {
                    posicaoMovimentoFogoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(500) - 200;
                    marcouPonto = false;
                }

                //Verifica pontuação
                if(posicaoMovimentoFogoHorizontal < 120 ){
                    if( !marcouPonto ){
                        pontuacao++;
                        marcouPonto = true;
                    }
                }

            }else{// Game Over
                //Zerar o valores padrões
                if( Gdx.input.justTouched() ){
                    estadoJogo = 0;
                    velocidadeQueda = 0;
                    pontuacao = 0;
                    posicaoMovimentoFogoHorizontal = larguraDispositivo;
                    posicaoInicialVertical = alturaDispositivo / 2;
                }

            }

        }

        //Configurar dados de projeção da câmera
        batch.setProjectionMatrix( camera.combined );

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(fogo1, posicaoMovimentoFogoHorizontal, alturaDispositivo / 2 + espacoEntreFogo / 2 + alturaEntreCanosRandomica);
        batch.draw(fogo2, posicaoMovimentoFogoHorizontal, alturaDispositivo / 3 + espacoEntreFogo / 2 + alturaEntreCanosRandomica);
        batch.draw(fogo3, posicaoMovimentoFogoHorizontal, alturaDispositivo / 4 - fogo1.getHeight() - espacoEntreFogo / 2 + alturaEntreCanosRandomica);
        batch.draw(fogo4, posicaoMovimentoFogoHorizontal, alturaDispositivo / 5 - fogo1.getHeight() - espacoEntreFogo / 2 + alturaEntreCanosRandomica);


        batch.draw(dragao[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if( estadoJogo == 2 ) {
            mensagem.draw(batch, "Toque para reiniciar!", larguraDispositivo / 2 - 210, alturaDispositivo / 2 - 100);
            mensagem.draw(batch, "Total de pontos: " + pontuacao, larguraDispositivo/2 - 210, alturaDispositivo/2);
            //batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
        }

        batch.end();

        dragaoCirculo.set(120 + dragao[0].getWidth() / 2, posicaoInicialVertical + dragao[0].getHeight() / 2, dragao[0].getWidth() / 2);

        retanguloFogo1 = new Rectangle(
                posicaoMovimentoFogoHorizontal, alturaDispositivo / 2 + espacoEntreFogo / 2 + alturaEntreCanosRandomica,
                fogo1.getWidth(), fogo1.getHeight()
        );

        retanguloFogo2 = new Rectangle(
                posicaoMovimentoFogoHorizontal, alturaDispositivo / 3 + espacoEntreFogo / 2 + alturaEntreCanosRandomica,
                fogo2.getWidth(), fogo2.getHeight()
        );

        retanguloFogo3 = new Rectangle(
                posicaoMovimentoFogoHorizontal, alturaDispositivo / 4 - fogo3.getHeight() - espacoEntreFogo / 2 + alturaEntreCanosRandomica,
                fogo3.getWidth(), fogo3.getHeight()
        );

        retanguloFogo4 = new Rectangle(
                posicaoMovimentoFogoHorizontal, alturaDispositivo / 5 - fogo4.getHeight() - espacoEntreFogo / 2 + alturaEntreCanosRandomica,
                fogo4.getWidth(), fogo4.getHeight()
        );

        //Teste de colisão
        if( Intersector.overlaps(dragaoCirculo, retanguloFogo3) || Intersector.overlaps(dragaoCirculo, retanguloFogo1)
                || Intersector.overlaps(dragaoCirculo, retanguloFogo4) || Intersector.overlaps(dragaoCirculo, retanguloFogo2)|| posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ){
            //Gdx.app.log("Colisão", "Houve colisão");
            estadoJogo = 2;
        }

    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}