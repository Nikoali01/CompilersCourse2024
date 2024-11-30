.class public RecordProcessor
.super java/lang/Object

# Определение рекорда (класс с полями id и name)
.class public Record
.super java/lang/Object

.field public id I
.field public name Ljava/lang/String;

.method public <init>(ILjava/lang/String;)V
    .limit stack 10
    .limit locals 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    aload_0
    iload_1
    putfield Record/id I
    aload_0
    aload_2
    putfield Record/name Ljava/lang/String;
    return
.end method

.method public toString()Ljava/lang/String;
    .limit stack 10
    .limit locals 10
    aload_0
    getfield Record/id I
    invokestatic java/lang/String/valueOf(I)Ljava/lang/String;
    ldc " - "
    aload_0
    getfield Record/name Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    areturn
.end method

# Главный метод для обработки рекордов
.method public static processRecord(LRecord;)LRecord;
    .limit stack 10
    .limit locals 10

    ; Получаем поле id и добавляем 1
    aload_0
    getfield Record/id I

