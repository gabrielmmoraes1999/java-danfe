# Java Danfe

Biblioteca Java 8 para gerar o DANFE a partir do XML da nota fiscal. Dois motores convivem no mesmo artefato:

- **Jasper** (`ImpressaoNfeUtil`): NF-e (modelo 55), NFC-e e CC-e, em PDF, HTML, preview ou impressão direta.
- **DANFE v2** (`ImpressaoDanfeV2Util`): somente NF-e modelo 55. O HTML é montado com Thymeleaf e o PDF com openhtmltopdf, no leiaute da NT 2026.010 (Reforma Tributária — IBS/CBS/IS). A orientação segue o `tpImp` do XML (retrato ou paisagem). Aceita XML autorizado (`nfeProc`) ou NF-e sem protocolo.

Repositório: [github.com/gabrielmmoraes1999/java-danfe](https://github.com/gabrielmmoraes1999/java-danfe)

## Dependência

```xml
<dependency>
    <groupId>io.github.gabrielmmoraes1999</groupId>
    <artifactId>java-danfe</artifactId>
    <version>2.0</version>
</dependency>
```

JasperReports, Thymeleaf e openhtmltopdf entram com escopo `provided`. Declare na aplicação só o motor que for usar.

Jasper (NF-e, NFC-e e CC-e):

```xml
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports</artifactId>
    <version>6.20.6</version>
</dependency>
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports-fonts</artifactId>
    <version>6.20.6</version>
</dependency>
```

DANFE v2:

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.1.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>com.openhtmltopdf</groupId>
    <artifactId>openhtmltopdf-pdfbox</artifactId>
    <version>1.0.10</version>
</dependency>
```

## Jasper

O XML é a fonte. `impressaoPadraoNFe` monta o layout padrão (logo, fatura e expressão XPath dos itens).

```java
String xml = XmlNfeUtil.leXml("nfe.xml");

ImpressaoDTO impressao = ImpressaoNfeUtil.impressaoPadraoNFe(xml);
ImpressaoNfeUtil.impressaoPdfArquivo(impressao, "danfe.pdf");
byte[] pdf = ImpressaoNfeUtil.impressaoPdfByte(impressao);
```

Também há HTML (`impressaoHtml`), preview (`impressaoPreview`) e impressão direta na impressora padrão ou em um `PrintService` (`impressaoDireta`).

NFC-e e carta de correção usam os mesmos métodos de saída, com o objeto padrão correspondente:

```java
ImpressaoDTO nfce = ImpressaoNfeUtil.impressaoPadraoNFCe(xml, urlConsulta);
ImpressaoDTO cce = ImpressaoNfeUtil.impressaoPadraoCCe(xml);
```

Para trocar o logo da NF-e, coloque outro `InputStream` no parâmetro `Logo` (`ConstantesImpressaoNfeUtil.PARAM_LOGO_NFE`). O mapa `getParametros()` aceita os demais parâmetros do relatório Jasper.

## DANFE v2

```java
ImpressaoDanfeV2DTO impressao = ImpressaoDanfeV2Util.impressaoPadraoNFe(xml);

ImpressaoDanfeV2Util.impressaoPdfArquivo(impressao, "danfe.pdf");
byte[] pdf = ImpressaoDanfeV2Util.impressaoPdfByte(impressao);
String html = ImpressaoDanfeV2Util.gerarHtml(impressao);
```

`impressaoHtml` grava o HTML em arquivo. `impressaoDireta` envia o PDF para a impressora padrão ou para um `PrintService`.

O objeto padrão já traz o logo de NF-e, orientação automática (`ide/tpImp`: 2 = paisagem) e canhoto. Ajustes:

```java
impressao.setLogoArquivo("logo.png"); // PNG, JPEG, GIF ou BMP
impressao.removerLogo();

impressao.setOrientacao(OrientacaoDanfeEnum.PAISAGEM); // ou RETRATO / AUTOMATICA
impressao.setExibirCanhoto(false);

impressao.setTemplate("caminho/meu-danfe.html");
impressao.getParametros().put(ImpressaoDanfeV2Util.PARAM_TIPO_REGIME_APURACAO, "valor");
```

`setTemplate(null)` volta ao template padrão (`classpath: /danfe/v2/danfe.html`). Um template próprio pode reaproveitar os fragmentos com `th:replace="~{fragmentos :: campo(...)}"`. Os valores de `getParametros()` ficam disponíveis como `${parametros.nome}`.

A NT 2026.010 reserva o campo "Tipo de Regime de Apuração do IBS e da CBS", mas a tag ainda não foi publicada. Ele sai vazio, a menos que a aplicação informe `PARAM_TIPO_REGIME_APURACAO`.

O openhtmltopdf escreve várias linhas INFO a cada PDF. A biblioteca não altera esse log. Para silenciar, chame `XRLog.setLevel` ou `XRLog.setLoggingEnabled(false)` na inicialização da aplicação.

## Erros

As falhas de geração, leitura de arquivo e impressão do DANFE v2 são lançadas como `DanfeException`. No motor Jasper, parte dos métodos declara as exceções do JasperReports e do parser XML; impressora ausente também vira `DanfeException`.

## Build

Requer JDK 8 e Maven.

```bash
mvn test
```

## Licença

[MIT](http://www.opensource.org/licenses/mit-license.php). Gabriel Moraes.
