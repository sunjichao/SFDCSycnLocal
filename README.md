# SalesforcSyncLocalDatabase

##Summary
    AUTHOR: sam sun

##Configration

###1. mapping csv 的配置
    Ⅰ. 在/csv 目录下需要插入你需要执行（Insert/Update）Salesforce 与本地数据库对应的mapping 关系；
    Ⅱ. 名称与config.property里  SalesforceToLocal/LocalToSalesforce 保持一致 如 Account.csv
    Ⅲ. csv文件的格式模板在/temp 路径下可以找到，第一列为Salesforce表，第二列为本地表
    Ⅳ. 请再三检查您的csv文件是否正确，salesforce/Local 是否存在该字段 是否对应

###2. config.property 的配置
    参看/temp 下的模板文件 检查路径是否正确

###3. Option
    Ⅰ. 本地数据库ID必须是自增的.
    Ⅱ. 暂不支持SQLServer
    Ⅲ. 依赖 Salesforce SOAP API
    Ⅳ. 不支持自由申请的developer账号
