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
    private Texture[] passaros;
    private Texture fundo;
    private Texture fogo1;
    private Texture fogo2;
    private Texture fogo3;
    private Texture fogo4;

    private Texture gameOver;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;

    private Rectangle retanguloFogoBaixo;
    private Rectangle retanguloFogoAlto;
    //private ShapeRenderer shape;

    //Atributos de configuracao
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo=0;// 0-> jogo não iniciado 1-> jogo iniciado 2-> Game Over
    private int pontuacao=0;

    private float variacao = 0;
    private float velocidadeQueda=0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
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
        passaroCirculo = new Circle();
        /*retanguloCanoTopo = new Rectangle();
        retanguloCanoBaixo = new Rectangle();
        shape = new ShapeRenderer();*/
        fonte = new BitmapFont();
        fonte.setColor(Color.BLUE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.BLUE);
        mensagem.getData().setScale(5);


        passaros = new Texture[4];
        passaros[0] = new Texture("dragao1.png");
        passaros[1] = new Texture("dragao2.png");
        passaros[2] = new Texture("dragao3.png");
        passaros[3] = new Texture("dragao4.png");

        fundo = new Texture("fundo1.png");
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
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 300;

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

            velocidadeQueda++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

            if( estadoJogo == 1 ){//iniciado

                posicaoMovimentoCanoHorizontal -= deltaTime * 500;

                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }

                //Verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -fogo2.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //Verifica pontuação
                if(posicaoMovimentoCanoHorizontal < 120 ){
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
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    posicaoInicialVertical = alturaDispositivo / 2;
                }

            }


        }

        //Configurar dados de projeção da câmera
        batch.setProjectionMatrix( camera.combined );

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(fogo2, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(fogo1, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - fogo1.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        batch.draw(fogo3, posicaoMovimentoCanoHorizontal, alturaDispositivo / 3 - fogo1.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(fogo4, posicaoMovimentoCanoHorizontal, alturaDispositivo / 3 + espacoEntreCanos / 3 + alturaEntreCanosRandomica);


        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if( estadoJogo == 2 ) {
            mensagem.draw(batch, "Toque para reiniciar!", larguraDispositivo / 2 - 300, alturaDispositivo / 2 - gameOver.getHeight());
            //batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
        }

        batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - fogo1.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                fogo1.getWidth(), fogo1.getHeight()
        );

        retanguloCanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                fogo2.getWidth(), fogo2.getHeight()
        );

        retanguloFogoAlto = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                fogo2.getWidth(), fogo2.getHeight()
        );

        retanguloFogoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - fogo1.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                fogo1.getWidth(), fogo1.getHeight()
        );


        //Desenhar formas
        /*shape.begin( ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
        shape.setColor(Color.RED);
        shape.end();*/

        //Teste de colisão
        if( Intersector.overlaps( passaroCirculo, retanguloCanoBaixo ) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
                || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo || Intersector.overlaps(passaroCirculo, retanguloFogoAlto) || Intersector.overlaps(passaroCirculo, retanguloFogoBaixo) ){
            //Gdx.app.log("Colisão", "Houve colisão");
            estadoJogo = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}