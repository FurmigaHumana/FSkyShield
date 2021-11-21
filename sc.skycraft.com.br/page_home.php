<?php

require_once('counters.php');

$style_compress = settings::$compress_css;
$script_compress = settings::$compress_js;

require_once(PATH . 'min/merger.php');

$scan = '@@REMOVED';
$download = '@@REMOVED';

?>
<!DOCTYPE html>
<!--[if IE 9 ]><html class="ie ie9" lang="en"><![endif]-->
<!--[if gt IE 9 | !IE]><!-->
<html lang="pt-br">
    <!--<![endif]-->
        
    <head>
	
        <?php
        echo '<base href="'. str_replace(DIRECTORY_SEPARATOR, '/', substr(__DIR__, strlen($_SERVER['DOCUMENT_ROOT']))) .'/">';
        ?>
		
        <meta charset="utf-8">
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
       
		<title>SkyShield - Sistema Anti-Hack</title>

        <meta name="description" content="A melhor e mais avançada proteção antihack, desenvolvida exclusivamente para a rede SkyCraft para prover uma jogabilidade justa, tranquila e livre de trapaceiros!">
        <meta name="keywords" content="skycraft, skyshield, vip, vip gratis, antihack, anti-hack">
		
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <link rel="apple-touch-icon" href="img/apple-touch-icon.png">

        <link rel="icon" type="image/png" href="img/favicon-32x32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="img/favicon-16x16.png" sizes="16x16">
        <link rel="icon" href="img/favicon.ico">
		
		<script>var loc = window.location.href+''; if (loc.indexOf('http://')==0){ window.location.href = loc.replace('http://','https://'); }</script>

        <?php
        
            $styles_main = array(
                "css/bootstrap.min.css",
                "css/vendor.css",
                "css/fonts.css",
                "css/style.css"
            );

            if (!$style_compress) {

                foreach ($styles_main as $style) {
                    echo "\n" . '<link href="' . $style . '" rel="stylesheet" type="text/css"/>';
                }

            } else {

                echo '<link href="' .  merge($styles_main, true) . '" rel="stylesheet" type="text/css"/>';

            }
        ?>
		
		
    </head>
    <body>

        <div class="site">


            <div class="site-canvas">
                <header class="site-header">
                    <nav class="navbar navbar-theme">
                        <div class="container">
                            <div class="navbar-header">
                                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse">
                                    <span class="sr-only">Alternar navegação</span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                </button>
                                <div class="navbar-brand-wrap">
                                    <a class="navbar-brand" href="#">
                                        <img src="img/site-header-logo.png" alt="">
                                    </a>
                                </div>
                            </div> <!-- .navbar-header -->

                            <div class="collapse navbar-collapse" id="navbar-collapse">
                                <ul class="nav navbar-nav navbar-right">
                                    <li class="active"><a href="#home">Inicio</a></li>
                                    <li><a href="#feature">Funções</a></li>
                                    <li><a href="#download">Instalar</a></li>
                                    <li><a style="color: #ff5454" href="@@REMOVED">SkyCraft <i class="fa fa-arrow-right"></i></a></li>
                                </ul>
                            </div> <!-- .navbar-collapse -->
                        </div>
                    </nav>
                </header> <!-- .site-header -->

                <main class="site-main">
                    <div id="home" class="section block-primary align-c p-b-0" style="background-color: #3a3939">
                        <div id="particles-js" class="site-bg">
                            <div class="site-bg-img"></div>
                            <div class="site-bg-video"></div>
                            <div class="site-bg-overlay"></div>
                            <div class="site-bg-animation layer" data-depth="0.30"></div>
                            <canvas class="site-bg-canvas layer" data-depth="0.30"></canvas>
                        </div> <!-- .site-bg -->

                        <div class="container">
                            <div class="row">
                                <div class="col-sm-12">
                                    <div class="col-inner" data-sr="bottom">
                                        <div class="section-heading">
                                            <h5>SkyShield • Anti Hack • SkyCraft</h5>
                                            <h1><?php echo number_format($connected, 0, ',', '.'); ?> jogadores online</h1>
                                            <p>Instale agora e colabore para criar uma comunidade livre de trapaceiros</p>
                                            </br>
                                        </div> <!-- .section-heading -->
                                        <div class="section-content">
                                            <a class="btn btn-default btn-outline m-a-5" href="#brief_1"><i class="fa fa-search"></i>Saiba mais</a>
                                            <a class="btn btn-danger m-a-5" href="#download"><i class="fa fa-download"></i>Instalar</a>
                                            <div class="m-x-auto m-t-40-xs-max m-t-60-sm-min" style="min-height: 100px;"></div>
                                        </div> <!-- .section-content -->
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> <!-- #home -->

                    <div id="brief_1" class="section block-light align-c-xs-max">
                        <div class="container">
                            <div class="row row-table">
                                <div class="col-sm-6">
                                    <div class="col-inner">
                                        <div class="section-heading align-c-xs-max">
                                            <h5>Não conhece o sistema?</h5>
                                            <h2>O que é SkyShield?</h2>
                                            <div class="divider"></div>
                                        </div> <!-- .section-heading -->
                                        <div class="section-content">
                                            <p>O SkyShield é uma ferramenta inovadora desenvolvida para combater hackers nos servidores de minecraft, diferente das outras ferramentas disponiveis, o SkyShield roda diretamente no computador do jogador o que evita uma infinidade de bugs e problemas na jogabilidade, resultando em uma partida limpa, sem defeitos, muito mais agradavel e divertida</p>
                                        </div> <!-- .section-content -->
                                    </div>
                                </div>
                                <div class="col-sm-6 m-t-60-xs-max p-l-45-sm-min p-l-75-md-min">
                                    <div class="col-inner clearfix">
                                        <img class="img-responsive float-r-sm-min m-x-auto-xs-max" src="img/item/brief-1.png" alt="" data-sr="right">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> <!-- #brief_1 -->

                    <div id="brief_2" class="section block-default align-c-xs-max">
                        <div class="container">
                            <div class="row row-table">
                                <div class="col-sm-6 m-b-60-xs-max p-r-45-sm-min p-r-75-md-min">
                                    <div class="col-inner clearfix">
                                        <img class="img-responsive float-l-sm-min m-x-auto-xs-max" src="img/item/brief-2.png" alt="" data-sr="left">
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="col-inner">
                                        <div class="section-heading align-c-xs-max">
                                            <h5>Com o SkyShield você ganha</h5>
                                            <h2>Vários beneficios!</h2>
                                        </div> <!-- .section-heading -->
                                        <div class="section-content">
                                            <ul class="list-unstyled">
                                                <li class="p-y-5"><i class="fa fa-check-square color-primary scale-125 m-r-10"></i> Ganhe o dobro ou até 4 vezes mais recompensas</li>
                                                <li class="p-y-5"><i class="fa fa-check-square color-primary scale-125 m-r-10"></i> Acesso a salas exclusivas totalmente protegidas</li>
                                                <li class="p-y-5"><i class="fa fa-check-square color-primary scale-125 m-r-10"></i> Desconto em vários itens como kits e roupas</li>
                                                <li class="p-y-5"><i class="fa fa-check-square color-primary scale-125 m-r-10"></i> <strong>Melhor jogabilidade e muito menos bugs</strong></li>
                                                <li class="p-y-5"><i class="fa fa-check-square color-primary scale-125 m-r-10"></i> ... e muitas outras vantagens!</li>
                                            </ul>
                                        </div> <!-- .section-content -->
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> <!-- #brief_2 -->

                    <div id="num" class="section block-primary align-c">
                        <div class="container">
                            <div class="row">
                                <div class="col-xs-6 col-sm-3 counter m-y-20-xs-max">
                                    <i class="fa fa-download color-default font-350"></i>
                                    <h3 class="m-t-10 m-b-5 font-250 count-num"><?php echo ($setups); ?></h3>
                                    <span class="font-bold">Instalações</span>
                                </div>
                                <div class="col-xs-6 col-sm-3 counter m-y-20-xs-max">
                                    <i class="fa fa-users color-default font-350"></i>
                                    <h3 class="m-t-10 m-b-5 font-250 count-num"><?php echo ($connected); ?></h3>
                                    <span class="font-bold">Online</span>
                                </div>
                                <div class="col-xs-6 col-sm-3 counter m-y-20-xs-max">
                                    <i class="fa fa-search color-default font-350"></i>
                                    <h3 class="m-t-10 m-b-5 font-250 count-num"><?php echo ($scans); ?></h3>
                                    <span class="font-bold">Verificações</span>
                                </div>
                                <div class="col-xs-6 col-sm-3 counter m-y-20-xs-max">
                                    <i class="fa fa-code color-default font-350"></i>
                                    <h3 class="m-t-10 m-b-5 font-250 count-num"><?php echo ($maxbuild); ?></h3>
                                    <span class="font-bold">Ultima versão</span>
                                </div>
                            </div>
                        </div>
                    </div> <!-- #num-->

                    <div id="feature" class="section block-light align-c">
                        <div class="container">
                            <div class="section-heading align-c">
                                <h5>Quais são os pontos fortes?</h5>
                                <h2>Informações basicas</h2>
                                <div class="divider"></div>
                                <p>Desenvolvido com tecnologia atual, o SkyShield veio para acabar com o problema de plugins defeituosos e proteções ineficazes contra hackers e trapaceiros</p>
                            </div>
                            <div class="row section-content m-t-70">
                                <div class="col-xs-12 col-sm-6 col-md-3 icon-box _center">
                                    <div class="icon-box-media">
                                        <img src="img/icon/rocket.png" alt="" data-sr="bottom">
                                    </div>
                                    <div class="icon-box-heading">
                                        <h3>Super Rápido</h3>
                                    </div>
                                    <div class="icon-box-content">
                                        <p>O SkyShield foi desenvolvido e testado para não atrapalhar as suas tarefas diarias, você nem vai notar ele executando!</p>
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3 icon-box _center">
                                    <div class="icon-box-media">
                                        <img src="img/icon/lock.png" alt="" data-sr="bottom">
                                    </div>
                                    <div class="icon-box-heading">
                                        <h3>100% Seguro</h3>
                                    </div>
                                    <div class="icon-box-content">
                                        <p>O SkyShield não coleta, não compartilha e não acessa o conteudo de nenhum de seus arquivos.</p>
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3 icon-box _center">
                                    <div class="icon-box-media">
                                        <img src="img/icon/setting.png" alt="" data-sr="bottom">
                                    </div>
                                    <div class="icon-box-heading">
                                        <h3>Compativel</h3>
                                    </div>
                                    <div class="icon-box-content">
                                        <p>Por ter sido feito em Java, o SkyShield é compativel com uma enorme quantidade de computadores.</p>
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3 icon-box _center">
                                    <div class="icon-box-media">
                                        <img src="img/icon/diamond.png" alt="" data-sr="bottom">
                                    </div>
                                    <div class="icon-box-heading">
                                        <h3>Gratuito!</h3>
                                    </div>
                                    <div class="icon-box-content">
                                        <p>Queremos o que é melhor para todos, portanto o SkyShield é totalmente grátis! não gaste um unico centavo para utilizar.</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> <!-- #feature-->

                    <div id="contact" class="section block-default">
                        <div class="container">
                            <div class="section-heading align-c">
                                <h5>Tem alguma duvida?</h5>
                                <h2>Acesse nosso fórum</h2>
                                </br>
                                <a class="btn btn-danger m-a-5" target="_blank" href="@@REMOVED">Fórum SkyCraft</a>

                            </div>
                        </div>
                    </div> <!-- #contact -->

                    <div id="download" class="section block-primary align-c">
                        
						<div class="container">
                            <p class="font-120 font-italic m-b-40">Baixe aqui e instale a ultima versão do SkyShield totalmente grátis, a instalação é simples, basta executar o arquivo e seguir as instruções, caso queira remover basta executar o mesmo arquivo novamente!</p>
                            <a class="btn btn-default btn-outline m-a-5" target="_blank" href="<?php echo $download; ?>"><i class="fa fa-windows"></i> Instalar</a>
                            <a class="btn btn-warning m-a-5" target="_blank" href="<?php echo $scan; ?>"><i class="fa fa-bug"></i> Virus Total</a>
                            </br>
                        </div>
						
                        <!-- skyshield-download -->
                        <ins class="adsbygoogle"
                             style="display:inline-block;width:970px;height:250px;margin-top:30px;"
                             data-ad-client="ca-pub-0734813023906538"
                             data-ad-slot="6207946514"></ins>

                    </div> <!-- #download-->
                </main> <!-- .site-main -->

                <footer id="siteFooter" class="site-footer block-invert">
                    <div class="site-footer-top">
                        <div class="container">
                            <div class="row">
                            </div>
                        </div>
                    </div> <!-- .site-footer-top -->

                    <div class="site-footer-bottom">
                        <div class="container">
                            <div class="row">
                                <div class="col-md-9">
                                    <ul class="site-footer-social-list">
                                        <li><a href="@@REMOVED" target="_blank">Facebook</a></li>
                                        <li><a href="@@REMOVED" target="_blank">Twitter</a></li>
                                        <li><a href="@@REMOVED" target="_blank">YouTube</a></li>
                                        <li><a href="@@REMOVED" target="_blank">Fórum</a></li>
                                        <li><a href="@@REMOVED" target="_blank">Loja</a></li>
                                    </ul> <!-- .site-footer-social-list -->
                                </div>
                            </div>
                        </div>
                    </div> <!-- .site-footer-bottom -->
                </footer> <!-- .site-footer -->
            </div>
        </div>
        <script>
        </script>
        <?php
        
            echo '<script type="text/javascript" src="' . merge(array('js/vendor/jquery-1.11.3.min.js'), false) . '"></script>';
        
            $scripts_main = array(
                "js/vendor/bootstrap.min.js",
                "js/vendor/scrollrevealjs.js",
                "js/vendor/jquery-appear.js",
                "js/vendor/counter.js",
                "js/vendor/particles.js",
                "js/vendor/velocityjs.js",
                "js/variable.js",
                "js/main.js"
            );

            if (!$script_compress) {

                foreach ($scripts_main as $script) {
                    echo "\n" .' <script type="text/javascript" src="' . $script . '"></script>';
                }

            } else {

                echo '<script type="text/javascript" src="' . merge($scripts_main, false) . '"></script>';

            }
        ?>
    </body>
</html>