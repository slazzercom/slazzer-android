# Slazzer-Auto Image Background Remover


![](https://github.com/slazzercom/Slazzer-Automatic-Remove-Image-Background-Android/blob/master/screenshot/slazzer_automatic_background_removal_android_app.gif)
* [Slazzer](https://slazzer.com/) Is a automatic background removal service just upload an image and get a clear transparent background
### Implementation
Add it in your project root build.gradel file

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Add it in your app label build.gradel file

    dependencies {
	        implementation 'com.github.slazzercom:slazzer-android:v1.0.0'
	}

 ### How to use Slazzer SDK
Initialize the SDK in your project label Application class. Find api key from  [here](https://slazzer.com/api)

    import android.app.Application
    import com.anthempest.salesapp.constant.Constants
    import com.slazzer.bgremover.Slazzer

    class SlazzerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Slazzer.init(Constants.API_KEY)
        
          }
    }
Then you can use your image file instaid of inputFile to get output image.

    Slazzer.get(inputFile,object :Slazzer.ResponseCallback{
           
            override fun onProgressStart() {
               //  will be invoked on progress start 
            }
            
            override fun onSuccess(response: Bitmap) {
                 //  will be invoked when progress done
            }
             override fun onProgressUpdate(percentage: Float) {
                runOnUiThread {
                   //  will be invoked when progress update 
                }

            }
            
            override fun onProgressEnd() {
              //  will be invoked when progress end 
            }
            
            override fun onError(errors: String) {
               //  will be invoked when error occurred
            }
    })
    
### Request Header

---
|key | Request Body   | Rrequired |
|---- | -----------  | --- |
|API-KEY | Enter your  [API-KEY](https://slazzer.com/api)  | Mandatory  | 

### Request Parameter

---
|key | Request Body   | Rrequired |
|---- | -----------  | --- |
|source_image_file (string($binary) ) | Source image file. Support only PNG, JPG, JPEG  | Mandatory  | 
|format (string) | Output image format. Default value 'png'. Otherwise 'jpg' for JPG format and 'png' for PNG format. 'png' format support alpha transparency.   |Optional | 
|bg_color_code (string) | Output with solid background color. Has to be HEX color code. (e.g. #72E4B3, #B3D472).   | Optional  |
|bg_image_file (string($binary) ) | Output with image file background. This image will be resize according to the output image file aspect ratio. supported bg_image_file formats are 'png', 'jpg', 'jpeg'.   | Optional |

#### Importent Note:
* Use any one background parameter("bg_color_code" or "bg_image_file") at a time when request API
### Error code from response

---
|Code | Response   | Details |
|---- | ----------  | --------- |
|200 | {"output_image_url": "image_url"}   | Successfully removed image background  | 
|400 | {"error": "Source image file not found"}   | Error: Invalid parameters or unable to process input image file (No credit deducted)  | 
|401 | {"error": "invalid api key"}   | Error: API-KEY missing or invalid API-KEY (No credit deducted)  |
|402 | {"error": "No credits remaining"}   | Error: No credits remaining (No credit deducted) |
|429 | {"error": "Api rate limit crossed"}   | Error: Api rate limit crossed (No credit deducted) |

### Sample Project
You can get complete sample code from this repository. Download the project and replace your [API-KEY](https://slazzer.com/api)

### Author
slazzercom
