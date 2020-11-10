/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.config;

import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * 配置调用Telcom api的Webclient
 *
 * @author wangyongjun
 * @date 2019/5/14.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class WebclientConfig {

    @Bean
    public WebClient telcomWebclient(WebClient.Builder webClientBuilder,
                                     @Value("${deliver.transport.telcom.env}") String telcomApiEnv) {
        try {
            // 客户端证书
            KeyStore selfCert = KeyStore.getInstance("pkcs12");
            selfCert.load(getClass().getResourceAsStream("/cert/outgoing.CertwithKey.pkcs12"),
                    "IoM@1234".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
            kmf.init(selfCert, "IoM@1234".toCharArray());

            // 服务端证书
            String telcomApiHost;
            String telcomApiCertificateName;
            String telcomApiCertificateStorePass;
            String telcomApiCertificateSign;
            if (TelcomApiConstant.TelcomEnv.test.toString().equals(telcomApiEnv)) {
                telcomApiHost = TelcomApiConstant.TELCOM_API_TEST_ENV_HOST;
                telcomApiCertificateName = TelcomApiConstant.TELCOM_API_TEST_ENV_CERT_FILE_NAME;
                telcomApiCertificateStorePass = TelcomApiConstant.TELCOM_API_TEST_ENV_CERT_FILE_PASS;
                telcomApiCertificateSign = TelcomApiConstant.TELCOM_API_TEST_ENV_CERT_SIGN;
            } else {
                telcomApiHost = TelcomApiConstant.TELCOM_API_PROD_ENV_HOST;
                telcomApiCertificateName = TelcomApiConstant.TELCOM_API_PROD_ENV_CERT_FILE_NAME;
                telcomApiCertificateStorePass = TelcomApiConstant.TELCOM_API_PROD_ENV_CERT_FILE_PASS;
                telcomApiCertificateSign = TelcomApiConstant.TELCOM_API_PROD_ENV_CERT_SIGN;
            }
            KeyStore caCert = KeyStore.getInstance("jks");
            caCert.load(getClass().getResourceAsStream("/cert/" + telcomApiCertificateName),
                    telcomApiCertificateStorePass.toCharArray());
            TrustManagerFactory tmf = new FingerprintTrustManagerFactory(telcomApiCertificateSign);

            SslContext sslContext = SslContextBuilder.forClient().keyManager(kmf).trustManager(tmf).build();
            HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
            ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
            return webClientBuilder.clientConnector(clientHttpConnector).baseUrl(telcomApiHost).build();
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
            log.error("Config webclient,error occurs", e);
            System.exit(-1);
        }
        return null;
    }
}