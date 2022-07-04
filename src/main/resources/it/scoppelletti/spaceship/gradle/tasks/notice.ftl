<#if projectTitle?has_content>
${projectTitle}
</#if>
Copyright(C) <#rt>
<#if inceptionYear?has_content>
${inceptionYear} <#rt>
</#if>
<#if developerName?has_content>
${developerName}, <#rt>
</#if>
<#if developerUrl?has_content>
<${developerUrl}/>
</#if>