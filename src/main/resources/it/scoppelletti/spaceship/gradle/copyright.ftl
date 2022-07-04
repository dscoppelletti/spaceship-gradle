Copyright(C) <#rt>
<#if inceptionYear?has_content>
${inceptionYear} <#rt>
</#if>
<#if developerUrl?has_content>
<#-- JSON strings cannot include " character -->
<a target='_blank' href='${developerUrl}'><#rt>
</#if>
<#if developerName?has_content>
${developerName}<#rt>
</#if>
<#if developerUrl?has_content>
</a>
</#if>
