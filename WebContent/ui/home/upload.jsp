<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div id="uploadStatus" style="display: none;">
	<div class="header">
		&nbsp;
		<span class="glyphicon glyphicon-save"></span>
		&nbsp;
		<span class="header_text">업로드 중...</span>
		<span id="close_uploadStatus" class="glyphicon glyphicon-remove pull-right"></span>
		<span id="resize_uploadList" class="glyphicon glyphicon-minus pull-right"></span>
	</div>
	<div class="body">
		<table role="presentation" class="table-striped">
			<tbody id="uploadList"></tbody>
		</table>
	</div>
</div>

<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<tr class="template-upload fade">
	{% if (false) { %}
		<td width="10%" align="center">
            <span class="preview"></span>
        </td>
	{% } else { %}
		<td width="5%"></td>
	{% } %}
        <td width="*">
            	<p class="name ellipsis" title="{%=file.name%}">{%=file.name%}</p>
            	{% if (file.error) { %}
                	<div><span class="label label-important ellipsis">Error</span> {%=file.errorMsg%}</div>
            	{% } %}
        </td>
		<td width="30%">
            {% if (!o.files.error) { %}
				<div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
        	{% } %}
        </td>
        <td width="15%" align="left">
            {% if (!o.files.error && !i && !o.options.autoUpload) { %}
				<span class="start button_icon_uploadStart_small btn">
		     		<span>시작</span>
		     	</span>
            {% } %}
            {% if (!i) { %}
				<a class="cancel upload_action_cancel" title="취소"></a>
            {% } %}
        </td>
    </tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<tr class="template-download fade">
	{% if (false) { %}
        <td width="10%" align="center">
            <span class="preview">
				 {% if (file.thumbnailUrl) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}"></a>
                {% } %}
			</span>
        </td>
	{% } else { %}
		<td width="5%"></td>
	{% } %}
        <td width="*">
            <p class="name ellipsis" title="{%=file.name%}">
				<span class="success">{%=file.name%}</span>
            </p>
        </td>
		<td width="15%" align="center">
            <span class="size"><small>{%=o.formatFileSize(file.size)%}</small></span>
        </td>
		<td width="10%" align="center">
			<div class="ellipsis">
            {% if (file.error && file.status=="aborted") { %}
				<span class="label label-warning">취소</span>
            {% } else if (file.error) { %}
				<span class="label label-danger">실패</span>
			{% } else { %}
				<span class="label label-success">성공</span>
			{% } %}
			</div>
        </td>
    </tr>
{% } %}
</script>
