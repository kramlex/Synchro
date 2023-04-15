grammar Multi;

prog:
    (context? set?)
;

// Контекст

context:
    (contextElement (SEMICOLON))+
;

boardSize:
    INT TIMES INT
;

typedValue:
    INT #intTypedValue
    | DOUBLE #doubleTypedValue
    | STRING #stringTypedValue
    | SYMBOL #symbolTypedValue
;

contextElement:
    (BOARD EQUALS boardSize) # boardExpr
    | (SYMBOL EQUALS typedValue) # asignExpr
;

// Набор

set:
    (name = SYMBOL EQUALS)? OPEN_SQUARE
        (
            (thread)
            | ((thread COLON)+thread)
        )
    CLOSE_SQUARE
;

// Поток

thread:
    (name = SYMBOL EQUALS)? OPEN_CIRCLE
        (action(SEMICOLON?) | ( (action SEMICOLON)+action(SEMICOLON?) ) )
    CLOSE_CIRCLE
;

// Действие

action:
    (name = SYMBOL EQUALS)? (
        body = actionBody
    )
;

actionBody:
    command # commandActionBody
    | (IF condition THEN action) #ifThenActionBody
    | (WAIT waitName = SYMBOL) #waitActionBody
    | (PAUSE durationSec = INT) #pauseActionBody
    | ((FOR repeatAction = action) (WHILE whileCondition = condition)) #forWhileBody
    | (REPEAT repeatCount = INT repeatAction = action) #repeatBody
    | ((FOR action) (WHILE condition) REPEAT (INT)? action ) #forActionBody
;

condition:
    namedCondition = SYMBOL
;

/// Команды

// базовые
base_command:
    START| STOP | LEFT | RIGHT | UP | DOWN
;

together_command:
    OPEN_SQUARE TOGETHER? (action | ((action COLON)+ action)) CLOSE_SQUARE
;

queue_command:
    OPEN_CIRCLE QUEUE? (action | ((action SEMICOLON)+ action)) CLOSE_CIRCLE
;

// все
command:
    base_command #baseCommand
    | together_command #togetherComand
    | queue_command #queueCommand
    | SYMBOL #symbolCommand
;

OPEN_CIRCLE: '(';
OPEN_SQUARE: '[';
CLOSE_CIRCLE: ')';
CLOSE_SQUARE: ']';
SEMICOLON: ';';
COLON: ',';
TIMES: '*';
EQUALS: '=';

// GAME BOARD

BOARD:
//    'Board'
    'Доска'
;

// KEYWORDS

PROBABLY:
//    'PROBABLY'
    'ВЕРОЯТНО'
;

TOGETHER:
//    'TOGETHER'
    'ВМЕСТЕ'
;

QUEUE:
//    'QUEUE'
    'ОЧЕРЕДЬ'
;

PAUSE:
//    'PAUSE'
    'ПАУЗА'
;

IF:
//    'IF'
    'ЕСЛИ'
;

THEN:
//    'THEN'
    'ТО'
;

WAIT:
//    'WAIT'
    'ЖДУ'
;

FOR:
//    'FOR'
    'ЦИКЛ'
;

WHILE:
//    'WHILE'
    'ПОКА'
;

REPEAT:
//    'REPEAT'
    'ПОВТОР'
;

START:
//    'START'
    'ПУСК'
;

STOP:
//    'STOP'
    'СТОП'
;

UP:
//    'UP'
    'ВВЕРХ'
;

DOWN:
//    'DOWN'
    'ВНИЗ'
;

LEFT:
//    'LEFT'
    'ВЛЕВО'
;

RIGHT:
//    'RIGHT'
    'ВПРАВО'
;

// TYPES

// NUMBERS

INT:
    [0-9]+
;

DOUBLE:
    [0-9]+.[0-9]+
;

// LITERA

SYMBOL:
    [а-яА-Яa-zA-Z0-9_]+
;

STRING:
    '"' .*? '"'
;

// SPACES

NEWLINE:
    '\r'? '\n' -> skip
;

WS:
    [ \t]+ -> skip
;
