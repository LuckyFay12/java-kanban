# java-kanban
Типы задач
----
Простейший кирпичик трекера — задача (англ. task). У неё есть следующие свойства:  
Название, кратко описывающее суть задачи (например, «Переезд»).  
Описание, в котором раскрываются детали.  
Уникальный идентификационный номер задачи, по которому её можно будет найти.  
Статус, отображающий её прогресс. Вы будете выделять следующие этапы жизни задачи, используя enum:  
1. NEW — задача только создана, но к её выполнению ещё не приступили.  
2. IN_PROGRESS — над задачей ведётся работа.  
3. DONE — задача выполнена.  

Менеджер
----
Рализован класс для объекта-менеджера. Он запускается на старте программы и управлет всеми задачами. Реализованы следующие функции:
Возможность хранить задачи всех типов. И методы для каждого из типа задач(Задача/Эпик/Подзадача).

Управление статусами осуществляется по следующему правилу:
----
 a. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.  
 b. Для эпиков:  
если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.  
если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.  
во всех остальных случаях статус должен быть IN_PROGRESS.  
