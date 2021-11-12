# Azure Database for MySQL Terraform Module

Azure Database to manage and scale. It automates the management  of infrastructure and database server, including,routine, updates,backups and security. 


## Module Usage

```hcl
module "mysql-db" {
  source  = "azuremysqlserver\terraform\service\back\module"
  

  # By default, this module will create a resource group
  # proivde a name to use an existing resource group and set the argument 
  # to `create_resource_group = false` if you want to existing resoruce group. 
  # If you use existing resrouce group location will be the same as existing RG.
  create_resource_group = false
  resource_group_name   = "rg-shared-westeurope-01"
  location              = "westeurope" // add all the variables for the here  
  -
  -
  -
  -
}
```


# Terraform Docker client for jenkins

build the docker client with  the makefile make build , make run and test.

usage:
make build 
make test
make save
make push
make clean

# Jenkinspipeline
 jenkinspipeline to checkout, terraform init ,build and apply  


