package br.com.caelum.leilao.dominio;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.infra.dao.Carteiro;
import br.com.caelum.leilao.infra.dao.EnviadorDeEmail;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.servico.EncerradorDeLeilao;
import org.junit.Test;
import org.mockito.InOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;
public class EncerradorDeLeilaoTest {

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
                .naData(antiga).constroi();
        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        LeilaoDao daoFalso = mock(LeilaoDao.class);
        when(daoFalso.correntes()).thenReturn(leiloesAntigos);

        EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);

        Carteiro carteirFalso= mock(Carteiro.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteirFalso);
        encerrador.encerra();

        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
        assertEquals(2, encerrador.getTotalEncerrados());

        verify(daoFalso,never()).atualiza(leilao1);
        verify(daoFalso,never()).atualiza(leilao2);
    }

    @Test
    public void naoDeveEncerrarLeilaoComMenosDeUmaSemana(){
        Calendar antiga = Calendar.getInstance();
        antiga.set(2018, 9, 30);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        List<Leilao> leiloesAntigos = Arrays.asList(leilao1);

        EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
        RepositorioDeLeiloes daoFalso = mock(LeilaoDao.class);
        when(daoFalso.correntes()).thenReturn(leiloesAntigos);

        Carteiro carteirFalso= mock(Carteiro.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteirFalso);
        encerrador.encerra();

        assertEquals(0,encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
    }

    @Test
    public void naoEncerrarLeilaoSeNaoHouver(){

        LeilaoDao daoFalso = mock(LeilaoDao.class);
        when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());

        EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
        Carteiro carteirFalso= mock(Carteiro.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteirFalso);
        encerrador.encerra();

        assertEquals(0,encerrador.getTotalEncerrados());

    }

    @Test
    public void testeLeilaoDao(){
        LeilaoDao leilaoTeste = mock(LeilaoDao.class);
        when(leilaoTeste.teste()).thenReturn(leilaoTeste.teste());
        assertEquals("teste", leilaoTeste.teste());
    }

    @Test
    public void deveAtualizarLeiloesEncerrado(){
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999,1,20);

        Leilao leilao1 = new CriadorDeLeilao().para("Tv de Plasma").naData(antiga).constroi();

        EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

        Carteiro carteirFalso= mock(Carteiro.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteirFalso);
        encerrador.encerra();

        verify(daoFalso,times(1)).atualiza(leilao1);
    }

    @Test
    public void emailsEnviadosNaOrdemCerta(){

        Calendar antiga = Calendar.getInstance();
        antiga.set(1999,1,20);

        Leilao leilao1 = new CriadorDeLeilao().para("Tv de Plasma").naData(antiga).constroi();

        EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

        Carteiro carteirFalso= mock(Carteiro.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteirFalso);
        encerrador.encerra();
        InOrder inOrder = inOrder(daoFalso, enviadorDeEmail);

        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);

    }
    @Test
    public void deveContinuarAExecucaoMesmoQuandoDaoFalha(){

        Calendar antiga = Calendar.getInstance();
        antiga.set(1999,1,20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
        Carteiro carteirofalso = mock(Carteiro.class);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1,leilao2));
//        doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1); // Simula o Lançamento de exeçoes
       //doThrow(new RuntimeException()).when(carteirofalso).envia(leilao1);
        //doThrow(new Exception()).when(carteirofalso).envia(leilao1);
        doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class)); //Não precisara ficar replicando codigo com o any(), qualquer leilao lançara runtime
        EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso,carteirofalso);
        encerradorDeLeilao.encerra();

       // verify(daoFalso).atualiza(leilao2);
       // verify(carteirofalso).envia(leilao2);

        verify(carteirofalso, never()).envia(any(Leilao.class));//verifica para todos os leiloes.
    }

//
//    atLeastOnce( ): o método deve ser executado pelomenos uma vez;
//
//    atLeast(n): o método deve ser executado no mínimo n vezes;
//
//    atMost(n): o método deve ser executado no máximo n vezes;
//
//    verify( ): verifica se o método foi executado.
}