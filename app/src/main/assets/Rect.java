Дан класс Rect, который реализует понятие "Прямоугольник".

class Rect {
    private double width, height;
    Rect(double width, double height){
        this.width = width;
        this.height = height;
    }   
}

Реализуйте конструктор класса ColorRect ("Цветной прямоугольник") - наследника класса Rect.

class ColorRect extends Rect {
   Color color;

   //YOUR CODE will be placed HERE
   //...
}

так, чтобы, например, код

ColorRect p = new ColorRect(500, 300, Color.RED);

cоздавал красный (т.е. чтобы третье переданное значение копировалось в color) прямоугольник со сторонами 500 и 300. В ответе приведите только код конструктора. Другие методы и поля добавлять не следует.
Ответ:(штрафной режим: 0, 0, 30, ... %)