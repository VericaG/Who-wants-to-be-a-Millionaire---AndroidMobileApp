# Who Wants to Be a Millionaire – Android Mobile Application

## 1. Вовед
Апликацијата е мобилна игра инспирирана од „Who Wants to Be a Millionaire“. Целта е да се овозможи:
- Генерирање на уникатни прашања преку API
- Управување со нивоа и добивки
- Користење на lifelines (50:50, Phone a Friend, Google Search)
- Приказ на Prize Ladder со звучни ефекти
- Управување со крај на играта (Quit, Wrong Answer, Winner)

---

## 2. Архитектура
- **Frontend:** Android (Kotlin, XML Layouts)  
- **State Management:** ViewModel + LiveData  
- **Backend:** OpenAI API за генерирање прашања  
- **Database:** Room (QuestionEntity, DAO)  
- **UI/UX:** Material Design, анимации со ObjectAnimator  
- **Audio:** SoundManager за ефекти  

---

## 3. Главни функционалности

### 3.1 Прашања
- Се вчитуваат преку API  
- Секое прашање има: текст, 4 одговори, индекс на точен одговор, ниво и тежина  

### 3.2 Нивоа и добивки
- Prize Ladder: 0 → 1,000,000  
- Safe Levels: 5 (1,000), 10 (32,000), 15 (1,000,000)  

### 3.3 Lifelines
- **50:50** – елиминира два погрешни одговори  
- **Phone a Friend** – повик до контакт од телефонски именик  
- **Google Search** – отвора пребарување со текстот на прашањето  

### 3.4 PrizeListFragment
- Приказ на тековно и следно ниво  
- Анимација на ново ниво  
- Автоматско враќање во QuizFragment  

### 3.5 EndActivity
- Приказ на крајна порака (Quit, Wrong Answer, Winner)  
- Приказ на освоена сума и достигнато ниво  

---

## 4. Структура на код
- **QuizViewModel** – управување со прашања, нивоа, добивки, API повици, lifelines  
- **QuizFragment** – UI за прашања и одговори, LiveData observers  
- **PrizeListFragment** – UI за Prize Ladder, анимации  
- **EndActivity** – UI за крај на играта  

---

## 5. Интеграции
- **OpenAI API** – генерирање уникатни прашања  
- **Contacts Provider** – пристап до контакти за Phone a Friend  
- **Google Search Intent** – пребарување на прашање  

---

## 6. Упатство за користење
1. Играчот започнува игра → се вчитува прашање  
2. Одговара на прашање → точен или погрешен одговор  
3. Ако е точен → PrizeListFragment → ново прашање  
4. Ако е погрешен → EndActivity со добивка од Safe Level  
5. Lifelines може да се користат еднаш  
