<html metal:use-macro="/WEB-INF/zpt/frame.html">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=<#if application.getProperty("templateEncoding")??>${application.getProperty("templateEncoding")}<#else>UTF-8</#if>" />
  <title metal:fill-slot="title">**タイトル**</title>
</head>
<body>
<div metal:fill-slot="body">
<h1>**見出し**</h1>
<ul tal:condition="notes">
  <li tal:repeat="note notes/notes" tal:content="note/%value">ここに必要に応じてメッセージが表示されます。</li>
</ul>
<p>**本文**</p>
</div>
</body>
</html>
