package com.artack2;

import com.artack2.interfaces.OrientationProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import sun.rmi.runtime.Log;

/**
 * Класс необходим для реализации управления датчиками ориентации.
 * */

class Cam extends PerspectiveCamera{

   // public static Vector3 startPos = new Vector3(2840838,2208903,5248416);	// Начальное положение 3D камеры в 3D мире
    public static Vector3 startPos ;	// Начальное положение 3D камеры в 3D мире

    Vector3 position;        // Позиция VR камеры
    Vector3 direction;       // Вектор направления VR камеры
    Vector3 up;              // Вектор UP VR камеры
    private Vector3 upDirCross;      // Векторное произведение up и direction (понадобится в части 2, сейчас не трогаем)

    private Vector3 defaultDirection;   // был 0 0 1
    private Vector3 defaultUp;          // был 0 1 0
    private Quaternion orientationQuaternion;
    float rotAngle = 0;//угол смещения системы координат
    /** Конструктор */
    Cam(float fov, float near, float far){
        super(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.near = near;
        this.far = far;
        //startPos = new Vector3(10, 0, 0);
        position = new Vector3(startPos);

        defaultDirection = new Vector3(0,0,0); // был 0 0 1
        defaultUp = new Vector3(0, 0, 1); // был 0 1 0

        direction = new Vector3(defaultDirection);
        up = new Vector3(defaultUp);
        upDirCross = new Vector3().set(direction).crs(up).nor();
        orientationQuaternion = new Quaternion();
    }

    /** Обновление ориентации камеры свежими orientationQuaternion */
    void update(OrientationProvider orientationProvider) {
        // Получили свежие изменения
        orientationProvider.getQuaternion(orientationQuaternion);

        //смещение системы координат при помощи углов Эйлера

        orientationQuaternion.setEulerAngles(orientationQuaternion.getYaw(),orientationQuaternion.getPitch()+rotAngle,orientationQuaternion.getRoll());
        // Из-за обхода стандартного механизма вращения камеры необходимо вручную
        // получать векторы ее направления из кватерниона
        direction.set(defaultDirection);
        orientationQuaternion.transform(direction);
        up.set(defaultUp);

        orientationQuaternion.transform(up);
        upDirCross.set(direction);
        upDirCross.crs(up).nor();

        // Надо бы вращать камеру кватернионом, а не углами
        // Вычисление углов вращения камер из кватерниона
        float angle = 2 * (float)Math.acos(orientationQuaternion.w);
        float s = 1f / (float)Math.sqrt(1 - orientationQuaternion.w * orientationQuaternion.w);
        float vx = orientationQuaternion.x * s;
        float vy = orientationQuaternion.y * s;
        float vz = orientationQuaternion.z * s;

        // Вращение камеры
        view.idt(); // Сброс матрицы вида

        view.rotateRad(vx, vy, vz, -angle); // Поворот кватернионом
        view.translate(-position.x, -position.y, -position.z); // Смещение в position
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);
/*
* view = {Matrix4@5166} "[1.0|0.0|0.0|-10.0]\n[0.0|1.0|0.0|-10.0]\n[0.0|0.0|1.0|-10.0]\n[0.0|0.0|0.0|1.0]\n"
combined = {Matrix4@5167} "[4.7248783|0.0|0.0|-47.248783]\n[0.0|2.847583|0.0|-28.47583]\n[0.0|0.0|-1.020202|8.181819]\n[0.0|0.0|-1.0|10.0]\n"
projection = {Matrix4@5169} "[4.7248783|0.0|0.0|0.0]\n[0.0|2.847583|0.0|0.0]\n[0.0|0.0|-1.020202|-2.020202]\n[0.0|0.0|-1.0|0.0]\n"
* */
    int x=1+1;
        //update();
        /* По какой-то причине, если здесь поставить update() , то combined меняется.
        * Хотя вроде как ручками всё рассчитали */
    }

/*
    Без update():
    combined = {Matrix4@11059} "[4.730152|0.0|0.0|0.0]\n[0.0|2.847583|0.0|0.0]\n[0.0|0.0|-1.020202|-2.020202]\n[0.0|0.0|-1.0|0.0]\n"
    projection = {Matrix4@11061} "[4.730152|0.0|0.0|0.0]\n[0.0|2.847583|0.0|0.0]\n[0.0|0.0|-1.020202|-2.020202]\n[0.0|0.0|-1.0|0.0]\n"

    С update():
    combined = {Matrix4@11060} "[-3.476261|3.207796|-5.638781E-7|2.6846483]\n[-3.3945835E-7|0.0|2.8475833|3.3945835E-6]\n[-0.6781727|-0.73492974|0.0|13.931023]\n[-0.6781592|-0.73491514|0.0|14.130743]\n"
    projection = {Matrix4@11062} "[4.730152|0.0|0.0|0.0]\n[0.0|2.847583|0.0|0.0]\n[0.0|0.0|-1.0000199|-0.20000198]\n[0.0|0.0|-1.0|0.0]\n"
*/

    /** Изменение местоположения камеры */
    void setPosition(Vector3 a) {
        position.set(a);
    }
    void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }
    void setPosition(float[] posVector) {
        position.set(posVector);
    }

    /** Возврат камеры */
    PerspectiveCamera getCam() {
        return this;
    }

    /** Возврат позиции, направления и вектора UP камеры, а так же их векторного произведения*/
    Vector3 getPosition() { return position; }
    Vector3 getDirection() { return super.direction; }
    Vector3 getUp() { return up; }
    Vector3 getUpDirCross() { return upDirCross; }
}