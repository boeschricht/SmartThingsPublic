/**
 *  ZHC5010 Z-Wave switch module test
 *
 *  Copyright 2018 Bo Eschricht. Based on original work by DarwinsDen.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Author: boeschricht@gmail.com
 *	Date: 2019-01-06
 *
 *	Changelog:
 *  1.2 (06-01-2019, boeschricht@gmail.com) -   Fixes and improvements:
 *                                                - Added GUI options to control encryption for endpoint communication
 *                                                - Updated fingerprint for better driver detection
 *                                                - Added GUI on/off button
 *                                                - Improved support for multichannel command class
 *                                                - Added command for controlling LED indicators programmatically and using GUI.
 *                                               To do:
 *                                                - multichannel
 *                                                - gui ep buttons for sim
 *                                                -
 *
 *  1.1	(boeschricht@gmail.com)                 Fixes:
 *						  - improved support for security command class required by firmware 2.03
 *  1.0 (02/07/2017, boeschricht@gmail.com) -  Major improvements of handler code for firmware 1.10.
 *                                              Improved Simulator support
 *                                              Fixes:
 *                                              Added support for command classes:
 *                                               - Central Scene - for better handling of button double-press, hold
 *                                               - Multi-level - dimming
 *                                               - Indicator - for control of LED's
 *                                              Added preference options for:
 *                                               - Enhanced control of relay mode
 *				                        		           - Control of LED indicator modes, LED "on"/"off" brightness levels
 *                                               - Disabling House Cleaning Mode (double press sends dimming command for 100% light level)
 *
 */


metadata {
	definition (name: "ZHC5010v3 fw 2.03 v1", namespace: "boeschricht", author: "boeschricht@gmail.com") {
// https://docs.smartthings.com/en/latest/capabilities-reference.html#switch
		capability "Actuator"
		capability "Switch"
		capability "Button"
		capability "Refresh"
		capability "Configuration"
		capability "Indicator"

		attribute "SecurityEnabled", "boolean"
		// attribute "LED1State", "enum", ["on", "off"]
		attribute "LED1State", "enum", ["on", "off"]
		attribute "LED2State", "enum", ["on", "off"]
		attribute "LED3State", "enum", ["on", "off"]
		attribute "LED4State", "enum", ["on", "off"]

		command "toggleSecurity"
		command "toggleLED1State"
		command "toggleLED2State"
		command "toggleLED3State"
		command "toggleLED4State"

		fingerprint type: "1001", mfr: "0234", prod: "0003", model: "010A", sec: "86"
	}

	// simulator metadata
	simulator {
	}

// https://docs.smartthings.com/en/latest/device-type-developers-guide/tiles-metadata.html
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
			state "off", label: '${name}', action: "switch.on", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		}
		standardTile("switchOn", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "on", label:'on', action:"switch.on", icon:"st.switches.switch.on"
		}
		standardTile("switchOff", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "off", label:'off', action:"switch.off", icon:"st.switches.switch.off"
		}
		// standardTile("ep1", "LED1State", width: 1, height: 1, canChangeIcon: true) {
		// 	state "on", label: 'ep 1', action: "toggleLED1State", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
		// 	state "off", label: 'ep 1', action: "toggleLED1State", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		// }
		standardTile("LED1", "LED1State", width: 1, height: 1, canChangeIcon: true) {
			state "on", label: 'LED 1', action: "toggleLED1State", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
			state "off", label: 'LED 1', action: "toggleLED1State", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		}
		standardTile("LED2", "LED2State", width: 1, height: 1, canChangeIcon: true) {
			state "on", label: 'LED 2', action: "toggleLED2State", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
			state "off", label: 'LED 2', action: "toggleLED2State", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		}
		standardTile("LED3", "LED3State", width: 1, height: 1, canChangeIcon: true) {
			state "on", label: 'LED 3', action: "toggleLED3State", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
			state "off", label: 'LED 3', action: "toggleLED3State", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		}
		standardTile("LED4", "LED4State", width: 1, height: 1, canChangeIcon: true) {
			state "on", label: 'LED 4', action: "toggleLED4State", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
			state "off", label: 'LED 4', action: "toggleLED4State", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		// standardTile("Disablesecurity", "SecurityEnabled", width: 1, height: 1) {
		// 	state "yes", label:'Security\ndisabled\n\n', action:"toggleSecurity", icon: "st.switches.switch.off", backgroundColor: "#00A0DC"
		// 	state "no", label:'Security\nenabled', action:"toggleSecurity", icon: "st.switches.switch.on", backgroundColor: "#ffffff"
		// }
		main "switch"
		// details (["switch", "switchOn", "switchOff", "ep1", "refresh", "Disablesecurity"])
		details (["switch", "switchOn", "switchOff", "LED1", "LED2", "LED3", "LED4", "refresh"])
	}

    preferences {
       input "doublePressCancelsSingle", "bool", title: "Cancel Single-Press when followed by Double-Press",  defaultValue: false,  displayDuringSetup: true, required: false
       input "SwitchRelayButtonNo", "number", title: "Physical relay mapped to button number (1-4)",  defaultValue: 1,  range: "1..4", displayDuringSetup: true, required: true
       input "disableSwitchRelay", "bool", title: "Disable the switch physical relay",  defaultValue: false,  displayDuringSetup: true, required: true
       input "disableLED1", "bool", title: "Disable LED #1",  defaultValue: false,  displayDuringSetup: true, required: true
       input "LED1BriOn", "number", title: "LED1 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED1BriOff", "number", title: "LED1 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
       input "disableLED2", "bool", title: "Disable LED #2",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED2BriOn", "number", title: "LED2 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED2BriOff", "number", title: "LED2 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
       input "disableLED3", "bool", title: "Disable LED #3",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED3BriOn", "number", title: "LED3 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED3BriOff", "number", title: "LED3 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
       input "disableLED4", "bool", title: "Disable LED #4",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED4BriOn", "number", title: "LED4 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED4BriOff", "number", title: "LED4 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
    }

}

def parse(String description) {
	log.debug("parse(). description: ${description}")
 	def result = null
 	def cmd = zwave.parse(description, [0x20: 1, 0x25: 1, 0x26: 2, 0x28: 1, 0x59: 1, 0x5A: 1, 0x5B: 1, 0x5E: 2, 0x60: 3, 0x70: 1, 0x72: 2, 0x73: 1, 0x7A: 2, 0x85: 2, 0x86: 1, 0x87: 1, 0x8E: 2, 0x98: 1])

    if (cmd) {
  		result = zwaveEvent(cmd)
		log.debug("parse(). result: $result")
  	}
    if (!result){
        log.debug "Parse returned ${result} for command ${cmd}"
    } else {
  		log.debug "Parse returned ${result}"
    }
 	return result
}

// -------------------------------------------------------------------------------
// events for supported classes
// -------------------------------------------------------------------------------

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
  	createEvent([name: "switch", value: cmd.value ? "on" : "off", type: "physical"])
}
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
  	createEvent([name: "switch", value: cmd.value ? "on" : "off", type: "physical"])
}
def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
 	createEvent([name: "switch", value: cmd.value ? "on" : "off", type: "digital"])
}
def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
  	if (state.manufacturer != cmd.manufacturerName) {
 		createEvent(updateDataValue("manufacturer", cmd.manufacturerName))
 	}
}
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    //log.debug "SecurityMessageEncapsulation. cmd: ${cmd}"
	def encapsulatedCommand = cmd.encapsulatedCommand([0x20: 1, 0x25: 1, 0x26: 2, 0x28: 1, 0x59: 1, 0x5A: 1, 0x5B: 1, 0x5E: 2, 0x60: 3, 0x70: 1, 0x72: 2, 0x73: 1, 0x7A: 2, 0x85: 2, 0x86: 1, 0x87: 1, 0x8E: 2, 0x98: 1])
    log.debug("encapsulatedCommand: ${encapsulatedCommand}")
	state.sec = 1
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	} else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		createEvent(descriptionText: cmd.toString())
	}
}
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
	response(configure())
}
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
    log.debug "unrecognized cmd: ${cnd}"
    createEvent([:])
}

// Support functions for scenenotification
def pressedButton (def btnRes) {
  def canceling = false

  if (state.doublePressed1 && btnRes ==1) {
     canceling = true
     state.doublePressed1 = false
  }
  else if (state.doublePressed2 && btnRes == 2) {
     canceling = true
     state.doublePressed2 = false
  }
  else if (state.doublePressed3 && btnRes == 3) {
     canceling = true
     state.doublePressed3 = false
  }
  else if (state.doublePressed4 && btnRes == 4) {
     canceling = true
     state.doublePressed4 = false
  }

  if (canceling) {
         //log.debug ("Canceling single press for button $btnRes")
         state.doublePressed=false
  }
  else
     {
         //log.debug ("button $btnRes pushed")
         sendEvent(name: "buttonNum" , value: "Btn: $btnRes pushed")
         sendEvent([name: "button", value: "pushed", data: [buttonNumber: "$btnRes"], descriptionText: "$device.displayName $btnRes pressed", isStateChange: true, type: "physical"])
       }
}
def pressedButton1() {
   pressedButton (1)
}
def pressedButton2() {
   pressedButton (2)
}
def pressedButton3() {
   pressedButton (3)
}
def pressedButton4() {
   pressedButton (4)
}
def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
    //log.debug("sceneNumber: ${cmd.sceneNumber} keyAttributes: ${cmd.keyAttributes}")
    def result = []
    switch (cmd.keyAttributes) {
       case 0:
           //pressed
           def buttonResult = cmd.sceneNumber
           if (doublePressCancelsSingle) {
             switch (buttonResult) {
               case 1:
                  state.doublePressed1=false
                  runIn (1, pressedButton1)
                  break
               case 2:
                  state.doublePressed2=false
                  runIn (1, pressedButton2)
                  break
               case 3:
                  state.doublePressed3=false
                  runIn (1, pressedButton3)
                  break
               case 4:
                  state.doublePressed4=false
                  runIn (1, pressedButton4)
                  break
               default:
                 //log.debug ("unexpected button $buttonNum")
                 break
             }
           } else {
				sendEvent(name: "buttonNum" , value: "Btn: $buttonResult pushed")
             	result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"],
                	descriptionText: "$device.displayName $buttonResult pressed", isStateChange: true, type: "physical"])
           }

           // if button pressed is button for physical relay, then toggle switch state
           if (! disableSwitchRelay) {
           		//log.debug("SwitchRelayButtonNo: $SwitchRelayButtonNo")
                //log.debug("buttonResult: ${buttonResult}")
                if (SwitchRelayButtonNo == $buttonResult) { // button for physical relay pressed
                	//log.debug("switch.currentSwitch: ${device.currentValue('switch')}")
                    if (device.currentValue('switch') == "on") {
	                    sendEvent(name: "switch" , value: "off")
                    } else {
	                    sendEvent(name: "switch" , value: "on")
                    }
                }
            }
            break

       case 1:
           //released
           def buttonResult = cmd.sceneNumber
           sendEvent(name: "buttonNum" , value: "Btn: $buttonResult released")
           result=createEvent([name: "button", value: "released", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult released", isStateChange: true, type: "physical"])
           break

       case 2:
           //held
           def buttonResult = cmd.sceneNumber
           result=createEvent([name: "button", value: "held", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult held", isStateChange: true, type: "physical"])
           break

       case 3:
           //double press
           def buttonResult = cmd.sceneNumber + 4

           switch (buttonResult) {
           case 5:
              state.doublePressed1=true
              break
           case 6:
              state.doublePressed2=true
              break
           case 7:
              state.doublePressed3=true
              break
           case 8:
              state.doublePressed4=true
              break
           default:
              log.debug ("unexpected double press button: $buttonResult")
           }

           sendEvent(name: "buttonNum" , value: "Btn: $buttonResult double press")
           result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult double-pressed", isStateChange: true, type: "physical"])
           break

	   case 4:
           //triple press -- not currently supported
           log.debug("tripple press")
           def buttonResult = cmd.sceneNumber + 8
           sendEvent(name: "buttonNum" , value: "Btn: $buttonResult double press")
           result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult double-pressed", isStateChange: true, type: "physical"])
           break

	  default:
           // unexpected case
           log.debug ("unexpected attribute: $cmd.keyAttributes")
   }
   return result
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointReport  cmd) {
  	return createEvent(descriptionText: "Number of endpoints: $cmd.endPoints. Type: ${ cmd.dynamic ? "dynamic" : "static"}. Identical: ${ cmd.identical }")

}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap  cmd) {
	// MultiChannelCmdEncap(destinationEndPoint: 0, parameter: [99, 0], sourceEndPoint: 1, command: 3, commandClass: 135, bitAddress: false)
	// log.debug("$cmd")
	// log.debug("cmd.command: $cmd.command")
	// log.debug("cmd.commandClass: $cmd.commandClass")
	def ep = cmd.sourceEndPoint
	def map = [:]
	if (cmd.commandClass == 0x87 && cmd.command == 3) {
		switch(ep) {
			case 1:
				map << [ name: "LED1State" ]
			break
			case 2:
				map << [ name: "LED2State" ]
			break
			case 3:
				map << [ name: "LED3State" ]
			break
			case 4:
				map << [ name: "LED4State" ]
			break
		}
		if (cmd.parameter == [0]) {
			map.value = "off"
		} else  {
			map.value = "on"
		}
		log.debug("map: $map")
		return map
	}
}


// -------------------------------------------------------------------------------
// commands dictated by "capability "Switch""
// -------------------------------------------------------------------------------
def on() {
//    def cmds = []
//    cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 3, size: 1).format()
//    delayBetween(cmds, 500)
	commands([zwave.basicV1.basicSet(value: 0xFF), zwave.basicV1.basicGet()], 1000)
}

def off() {
	commands([zwave.basicV1.basicSet(value: 0x00), zwave.basicV1.basicGet()], 1000)
}

// -------------------------------------------------------------------------------
// commands dictated by "capability "Configuration""
// -------------------------------------------------------------------------------
def configure() {
    // update numberOfButtons attribute from switch capability
	sendEvent(name: "numberOfButtons", value: 12, displayed: false)
    commands([
		zwave.configurationV1.configurationSet(configurationValue: disableLED1 ? [0] : [1], parameterNumber: 3, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: disableLED2 ? [0] : [1], parameterNumber: 4, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: disableLED3 ? [0] : [1], parameterNumber: 5, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: disableLED4 ? [0] : [1], parameterNumber: 6, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED1BriOn], parameterNumber: 7, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED2BriOn], parameterNumber: 8, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED3BriOn], parameterNumber: 9, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED4BriOn], parameterNumber: 10, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED1BriOff], parameterNumber: 11, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED2BriOff], parameterNumber: 12, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED3BriOff], parameterNumber: 13, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [LED4BriOff], parameterNumber: 14, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: disableSwitchRelay ? [0] : [SwitchRelayButtonNo], parameterNumber: 15, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 33, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 34, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 35, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 36, size: 1 )
		zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 33, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 34, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 35, size: 1 ),
		zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 36, size: 1 )
	], 2000)
}

// -------------------------------------------------------------------------------
// Updated routine, called when changing preferences
// -------------------------------------------------------------------------------

def updated() {
	log.debug("updated()")
    // def cmds = []
    // commands([
		// zwave.configurationV1.configurationSet(configurationValue: disableLED1 ? [0] : [1], parameterNumber: 3, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: disableLED2 ? [0] : [1], parameterNumber: 4, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: disableLED3 ? [0] : [1], parameterNumber: 5, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: disableLED4 ? [0] : [1], parameterNumber: 6, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED1BriOn], parameterNumber: 7, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED2BriOn], parameterNumber: 8, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED3BriOn], parameterNumber: 9, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED4BriOn], parameterNumber: 10, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED1BriOff], parameterNumber: 11, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED2BriOff], parameterNumber: 12, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED3BriOff], parameterNumber: 13, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [LED4BriOff], parameterNumber: 14, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: disableSwitchRelay ? [0] : [SwitchRelayButtonNo], parameterNumber: 15, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 33, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 34, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 35, size: 1 ),
		// zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 36, size: 1 )
	// ], 2000)
}

def refresh() {
	log.debug("refresh()")
	log.debug("her2")
	commands([
//		zwave.multiChannelV3.multiChannelEndPointGet(),
//		zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: 1, commandClass: 0x25, command: 1, parameter: [0]),
//		zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: 1, commandClass: 0x25, command: 2),
		zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: 1, commandClass: 0x87, command: 1, parameter: [0xFF]),
		zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: 1, commandClass: 0x87, command: 2)
	], 2000)
}

// def toggleSecurity() {
// 	log.debug("device.currentValue('SecurityEnabled'): ${device.currentValue('SecurityEnabled')}")
//
// 	if (device.currentValue('SecurityEnabled') == "yes") {
// 	    commands([
// 			zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 33, size: 1 ),
// 			zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 34, size: 1 ),
// 			zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 35, size: 1 ),
// 			zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber: 36, size: 1 )
// 		], 2000)
//
// 		sendEvent(name: "SecurityEnabled" , value: "no")
// 	} else {
// 	    commands([
// 			zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 33, size: 1 ),
// 			zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 34, size: 1 ),
// 			zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 35, size: 1 ),
// 			zwave.configurationV1.configurationSet(configurationValue: [0x3F], parameterNumber: 36, size: 1 )
// 		], 2000)
//
// 		sendEvent(name: "SecurityEnabled" , value: "yes")
// 	}
// 	log.debug("device.currentValue('SecurityEnabled'): ${device.currentValue('SecurityEnabled')}")
// }

def toggleLED1State() {
	toggleLEDState(1)
}
def toggleLED2State() {
	toggleLEDState(2)
}
def toggleLED3State() {
	toggleLEDState(3)
}
def toggleLED4State() {
	toggleLEDState(4)
}

def toggleLEDState(LEDnum) {
        // log.debug("device.currentValue('LED1State'): ${device.currentValue('LED1State')}")
	def sAttribute = ""
	switch (LEDnum) {
		case 1:
			sAttribute = "LED1State"
		break
		case 2:
			sAttribute = "LED2State"
		break
		case 3:
			sAttribute = "LED3State"
		break
		case 4:
			sAttribute = "LED4State"
		break
	}
	if (device.currentValue(sAttribute) == "on") {
		sendEvent(name: "$sAttribute" , value: "off")

		commands([zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: LEDnum, commandClass: 0x87, command: 1, parameter: [0]),
			zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: LEDnum, commandClass: 0x87, command: 2)
		], 2000)

	} else {
		sendEvent(name: "$sAttribute" , value: "on")

		commands([zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: LEDnum, commandClass: 0x87, command: 1, parameter: [0xFF]),
			zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: false, destinationEndPoint: LEDnum, commandClass: 0x87, command: 2)
		], 2000)
	}
}


private command(physicalgraph.zwave.Command cmd) {
	log.debug("state.sec: ${state.sec}")
    if (state.sec) {
		log.debug("cmd: ${cmd} ")
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private commands(commands, delay=250) {
	delayBetween(commands.collect{ command(it) }, delay)
}
