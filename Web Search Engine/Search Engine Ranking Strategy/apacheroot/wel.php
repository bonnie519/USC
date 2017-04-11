<?php
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$check = false;
$results = false;
//read mapping from data.json
$file = fopen("data.json", "r");
$content = "";
    while(!feof($file)){
        $content.= fgets($file);
    }
fclose($file);
$content = json_decode($content,true);

//submit query to solr server
if ($query) {
    require_once('Apache/Solr/Service.php');
    $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myhw/');
    if (get_magic_quotes_gpc() == 1) {
        $query = stripslashes($query);
    }
    $param = [];
    if (array_key_exists("pageRank", $_REQUEST)) {
	$check = $_REQUEST['pageRank']==on?"checked":"";//save the checked status after submit        
	$param['sort'] ="pageRankFile desc";//choose PageRank
    }
    $results = $solr->search($query, 0, $limit, $param);
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>PHP Solr Search Engine</title>
	<link rel="stylesheet" href="bootstrap.min.css">
	<script type="text/javascript" src="jquery-3.2.0.min.js"></script>
	<script type="text/javascript" src="bootstrap.min.js"></script>
	<style type="text/css">
		html,body,div{margin:0;padding:0;}
		#searchbar{margin-bottom:20px;margin-left:5%;margin-top:15px;}
		#contnt{margin-left:10%;}
		#link{font-size:12px;font-family:Arial;}
		#link a:link{color:green;}
		.myres{margin-top:15px;align:left;margin-right:28%;}
	</style>
</head>
<body>
<div id="searchbar">
	<form method="get" class="form-inline" accept-charset="utf-8">
		<label for="q" style="margin-right:2%;">Solr</label>
		<input id="q" name="q" type="text" class="form-control" style="margin-right:0;width:200px;
border-top-left-radius:5px;border-bottom-left-radius:5px;
border-top-right-radius:0;border-bottom-right-radius:0;" placeholder="Please enter" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
		<span><button type="submit" class="btn btn-info" style="margin-left:0;
border-top-right-radius:5px;border-bottom-right-radius:5px;
border-top-left-radius:0;border-bottom-left-radius:0;">-></button></span>
		<input type="checkbox" name="pageRank" style="margin-left:10px;"<?php echo $check;?> />
		<label>&nbsp;Use PageRank</label>
	</form>
</div>
    <!--get results and display-->
    <?php if ($results): ?>
        <?php 
            $total = (int)$results->response->numFound;
            $start = min(1, $total);
            $end = min($limit, $total);
        ?>
	<div id="contnt">
        <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
        <?php foreach ($results->response->docs as $doc): ?>
            <?php 
                $id = $doc->id;
				$sid = substr($id,39);//get the short encoded url from ID
				$realurl = $content[$sid];//get the real url from json object
                $url = urldecode($realurl);
            ?>
            <div class="myres">
				<a href="<?php echo $url; ?>"><?php echo $doc->title ? $doc->title : "None"; ?></a>
				<div id="link"><a href="<?php echo $url; ?>"><?php echo $realurl; ?></a></div>
				<div id="id" style="font-size:12px;">ID:&nbsp;<?php echo $id; ?></div>
				<div style="font-size:12px;font-color:black;">
					Description: <?php echo $doc->description ? $doc->description : "None"; ?>
				</div>
			</div>
        <?php endforeach; ?>
    </div>
	<?php endif; ?>
</body>
</html>