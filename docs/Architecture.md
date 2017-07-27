# AlvisNLP/ML architecture

Alvisnlp is composed of two principal level, alvisnlp-core and alvisnlp-bibliome.


![Architecture](assets/images/architecture.png)



alvisnlp-bibliome is to maintain the libraries of modules that alvis provides. These modules are elementary and autonomous program ascompagnied with their interfaces and their human descriptions.

alvisnlp-core manage the utilization of alvisnlp-bibliome. It provides resources to recognize a module, load it and execute it from alvisnlp-biliome. It hold the [shared data structure](alvis_internal_data_model.md) that all the modules access during execution. It also manage the interpretation of the plans and the sequential execution of each module. A [plan](define-and-run-alvis-plans.md) is a pipline, a list of modules and their parameters to execute in a sequential mode.