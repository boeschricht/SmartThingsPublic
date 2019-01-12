/*
 *  ZHC5010 Z-Wave switch module test
 *
 *  Copyright 2016 DarwinsDen.com
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
 *	Author: Darwin@DarwinsDen.com
 *	Date: 2016-06-08
 *
 *	Changelog:
 *
 *	0.01 (06/08/2016) -	Initial 0.01 Test Code/Beta
 *	0.02 (08/04/2016) -	Added double and triple tap (increments button by +4, and +8 respectively)
 *	0.03 (12/23/2016) -	Added test/workaround preference option to cancel single press after double press. Added
 *                      preference option to disable switch relay
 *
 */
 
metadata {
	definition (name: "ZHC5010", namespace: "boeschricht", author: "darwin@darwinsden.com, boeschricht@gmail.com") {
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
        capability "Configuration"
        capability "Zw Multichannel"
          
 		fingerprint type:"1001"
		fingerprint mfr: "0234", prod: "0003", model: "010A" 
//debug ManufacturerSpecificReport(manufacturerId: 564, manufacturerName: null, productId: 266, productTypeId: 3)
//debug parse(zw device: 0F, command: 7205, payload: 02 34 00 03 01 0A )
        
}

	// simulator metadata
	simulator {
        status "on": "command: 2003, payload: FF"
        status "off": "command: 2003, payload: 00"

        // reply messages
        reply "2001FF,delay 100,2502": "command: 2503, payload: FF"
        reply "200100,delay 100,2502": "command: 2503, payload: 00"
	}
    
    preferences {      
    	input "iled1mode", number, title: "LED #1 mode", defaultValue: 1, displayDuringSetup:false, required:false
    	input "led2mode", integer, title: "LED #2 mode", defaultValue: 1, displayDuringSetup:false, required:false
    	input "led3mode", integer, title: "LED #3 mode", defaultValue: 1, displayDuringSetup:false, required:false
    	input "led4mode", integer, title: "LED #4 mode", defaultValue: 1, displayDuringSetup:false, required:false
    	input "led1bri", integer, title: "LED #1 brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led2bri", integer, title: "LED #2 brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led3bri", integer, title: "LED #3 brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led4bri", integer, title: "LED #4 brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led1offbri", integer, title: "LED #1 off brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led2bri", integer, title: "LED #2 off brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led3offbri", integer, title: "LED #3 off brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
    	input "led4offbri", integer, title: "LED #4 off brightness", defaultValue: 50, displayDuringSetup:false, required:false // 0-100
		input "physicalRelayButton", "integer", title: "Button number to control physical relay", defaultValue:1, displayDuringSetup:false, required:false
		input "disableHouseCleaningMode", "bool", title: "House cleaning mode disabled", defaultValue:false, displayDuringSetup:false, required:false
       	input "doublePressCancelsSingle", "bool", title: "Cancel Single-Press when followed by Double-Press",  defaultValue: false,  displayDuringSetup: true, required: false	       
       	input "disableSwitchRelay", "bool", title: "Disable the switch physical relay",  defaultValue: false,  displayDuringSetup: true, required: false	       
    }

	tiles(scale: 2) {
 		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.Home.home30", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.Home.home30", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.Home.home30", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.Home.home30", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
            tileAttribute("device.status", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'${currentValue}', unit:"")
            }
		}
   
       	valueTile("buttonNum", "device.buttonNum", width: 2, height: 2) {
			state("", label:'${currentValue}')
		}

         valueTile("firmwareVersion", "device.firmwareVersion", width:2, height: 2, decoration: "flat", inactiveLabel: false) {
			state "default", label: '${currentValue}'
		}
        
         valueTile("deviceID", "device.deviceID", width:2, height: 2, decoration: "flat", inactiveLabel: false) {
			state "default", label: '${currentValue}'
		}
        
		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		}
        
		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
		}
        standardTile("configure", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:"", action:"configure", icon:"st.secondary.configure"
        }
 

		main (["switch","level",  "buttonNum","deviceID", "firwareVersion","refresh", "configure"])
	}
}



def parse(String description) {
	log.debug "parse(${description})"
 	def result = null
 	def cmd = zwave.parse(description)
	
    if (cmd) {
  		result = zwaveEvent(cmd)
  	}
    if (!result){
        log.debug "Parse returned ${result} for command ${cmd}"
    }
    else {
  		log.debug "Parse returned ${result}"
    }   
 	return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
    /*def result
    if (cmd.value == 0) {
        result = createEvent(name: "switch", value: "off")
    } else {
        result = createEvent(name: "switch", value: "on")
    }
    return result*/
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    //result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinarySet cmd) {
	log.debug("SwitchBinarySet(): ${cmd}")
	
		return zwaveEvent(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
    sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:4, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}


def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd) 
{
    log.debug "multichannelv3.MultiChannelCapabilityReport $cmd"
    if (cmd.endPoint == 2 ) {
        def currstate = device.currentState("switch2").getValue()
        if (currstate == "on")
        	sendEvent(name: "switch2", value: "off", isStateChange: true, display: false)
        else if (currstate == "off")
        	sendEvent(name: "switch2", value: "on", isStateChange: true, display: false)
    }
    else if (cmd.endPoint == 1 ) {
        def currstate = device.currentState("switch1").getValue()
        if (currstate == "on")
        sendEvent(name: "switch1", value: "off", isStateChange: true, display: false)
        else if (currstate == "off")
        sendEvent(name: "switch1", value: "on", isStateChange: true, display: false)
    }
}


def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    log.debug "multichannelv3.MultiChannelCmdEncap $cmd"
   def map = [ name: "switch$cmd.sourceEndPoint" ]
	def encapsulatedCommand = cmd.encapsulatedCommand([0x30: 1, 0x31: 1])
	log.debug ("Command from endpoint ${cmd.sourceEndPoint}: ${encapsulatedCommand}")

	if (encapsulatedCommand) {
		return zwaveEvent(encapsulatedCommand)
	}
   
   // switch(cmd.commandClass) {
      // case 32:
         // if (cmd.parameter == [0]) {
            // map.value = "off"
         // }
         // if (cmd.parameter == [255]) {
            // map.value = "on"
         // }
         // createEvent(map)
         // break
      // case 37:
         // if (cmd.parameter == [0]) {
            // map.value = "off"
         // }
         // if (cmd.parameter == [255]) {
            // map.value = "on"
         // }
         // createEvent(map)
         // break
    // }
	
	
}

def zwaveEvent(physicalgraph.zwave.commands.hailv1.Hail cmd) {
 	createEvent([name: "hail", value: "hail", descriptionText: "Switch button was pressed", displayed: false])
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	log.debug (cmd)
	if (state.manufacturer != cmd.manufacturerId) {
 		createEvent(updateDataValue("manufacturer", cmd.manufacturerId.toString()))
 	}
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
	log.debug ("ConfigurationReport($cmd)")
	Integer value = (cmd.configurationValue[0])
	switch (cmd.parameterNumber) {
        case 3:
           	return createEvent( name: "led1mode", value: value )
/*    	case 4:
           	return createEvent( name: "led2mode", value: value )
    	case 5:
           	return createEvent( name: "led3mode", value: value )
    	case 6:
           	return createEvent( name: "led4mode", value: value )
*/    }
//xxx
}
def zwaveEvent(physicalgraph.zwave.Command cmd) {
    // This will capture any commands not handled by other instances of zwaveEvent
    // and is recommended for development so you can see every command the device sends
    debug.log("Command ${cmd.command}")
    if (cmd == "5E02") {
    	log.debug("ZWAVEPLUS_INFO_REPORT received")
    }
    if (cmd == "7006") {
    	log.debug("CONFIGURATION_REPORT received")
    }
    return createEvent(descriptionText: "${device.displayName}: ${cmd}")
	
}

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
         log.debug ("Canceling single press for button $btnRes")
         state.doublePressed=false
  }
  else
     {
         log.debug ("button $btnRes pushed")
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
    log.debug("sceneNumber: ${cmd.sceneNumber} keyAttributes: ${cmd.keyAttributes}")
    def result = []

    switch (cmd.keyAttributes) {
       case 0:
           //pressed
           def buttonResult = cmd.sceneNumber
           if (doublePressCancelsSingle)
           {
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
                 log.debug ("unexpected button $buttonNum")
             }
           }
           else
           {
             sendEvent(name: "buttonNum" , value: "Btn: $buttonResult pushed")
             result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"], 
                descriptionText: "$device.displayName $buttonResult pressed", isStateChange: true, type: "physical"])
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
           def buttonResult = cmd.sceneNumber + 8
           s endEvent(name: "buttonNum" , value: "Btn: $buttonResult double press")
           result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"], 
                         descriptionText: "$device.displayName $buttonResult double-pressed", isStateChange: true, type: "physical"])
           break                  


      default:
           // unexpected case


           log.debug ("unexpected attribute: $cmd.keyAttributes")
   }  
   return result
}

def configure() {
	log.debug "configure() called"

	// todo: this is for determining if device supports config v2. If we do this, we should also act on result. Commenting out for now.
    def cmds = []
    // cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()
    // cmds << zwave.configurationV2.configurationSet(parameterNumber: 3, configurationValue: [3]).format()	
    // cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()

	
	sendEvent(name: "numberOfButtons", value: 12, displayed: false)
     // if (disableSwitchRelay) {
        // zwave.configurationV2.configurationSet(configurationValue: [1], parameterNumber: 15, size: 1).format()
     // }
    // if ( cmds != [] && cmds != null ) return delayBetween(cmds, 2000) else return
}

def poll() {
	refresh
}
    
def refresh() {
	log.debug "refresh()"
	
	// todo: remove this call later 
	configure()
	def cmds = []
    
	// cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet().format()
	// todo: implement: #define MANUFACTURER_SPECIFIC_REPORT 0x05. 
	// return manu, prod type, prod id used for fingerprinting
	//. command: 7205, payload: 02 34 00 03 01 0A 
	
    // cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()	// LED #1 mode
/*    cmds << zwave.configurationV2.configurationGet(parameterNumber: 4).format()	// LED #2 mode
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 5).format()	// LED #3 mode
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 6).format()	// LED #4 mode
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 7).format()	// LED #1 brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 8).format()	// LED #2 brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 9).format()	// LED #3 brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 10).format()	// LED #4 brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 11).format()	// LED #1 off brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 12).format()	// LED #2 off brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 13).format()	// LED #3 off brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 14).format()	// LED #4 off brightness
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 15).format()	// physical relay mode
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 18).format()	// disable house cleaning mode
*/    //xxx

	// Get state of switches
	// cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:0, destinationEndPoint:1, commandClass:37, command:2).format() //0x25 COMMAND_CLASS_SWITCH_BINARY, 0x2 = GET
    // cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:0, destinationEndPoint:2, commandClass:37, command:2).format()
    // cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:0, destinationEndPoint:3, commandClass:37, command:2).format()
    // cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:0, destinationEndPoint:4, commandClass:37, command:2).format()
	// delayBetween(cmds, 1000)
	
	//Press on for switch 0 (should affect physical switch)  // nope, not when device is in multichannel mode (default)
	// def turnoncmd = physicalgraph.zwave.commands.switchbinaryv1.SwitchBinarySet(switchValue:1).format() //0x25 
	// COMMAND_CLASS_SWITCH_BINARY, 0x1 = SET

	cmds = []
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.switchBinaryV1.switchBinarySet(switchValue: 0x1))
	delayBetween(cmds, 1000)
	// log.debug("her: ${turnoncmd}")
	
}

def updated()
{
	log.debug "Preferences have been changed. Attempting configure()"
    def cmds = configure()
    response(cmds)
}

