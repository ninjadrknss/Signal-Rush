from machine import Pin

left_player_inputs = [
    Pin(3, Pin.IN, Pin.PULL_UP), # up
    Pin(2, Pin.IN, Pin.PULL_UP), # down
    Pin(5, Pin.IN, Pin.PULL_UP), # left
    Pin(4, Pin.IN, Pin.PULL_UP), # right
    Pin(21, Pin.IN, Pin.PULL_UP), # button 1
    Pin(20, Pin.IN, Pin.PULL_UP), # button 2
]

right_player_inputs = [
    Pin(11, Pin.IN, Pin.PULL_UP), # up
    Pin(10, Pin.IN, Pin.PULL_UP), # down
    Pin(13, Pin.IN, Pin.PULL_UP), # left
    Pin(12, Pin.IN, Pin.PULL_UP), # right
    Pin(17, Pin.IN, Pin.PULL_UP), # button 1
    Pin(16, Pin.IN, Pin.PULL_UP), # button 2
]

inputs = [0] * 12

while True:
    for i in range(len(left_player_inputs)):
        inputs[i] = 1 - left_player_inputs[i].value() # normally open

    for i in range(len(right_player_inputs)):
        inputs[i+6] = 1 - right_player_inputs[i].value()  # normally open

    print(f"P:{''.join(str(int(b)) for b in inputs)}")
