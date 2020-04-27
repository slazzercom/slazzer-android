# Slazzer-Auto Image Background Remover

* [Slazzer](https://slazzer.com/) provides free service to remove background of any photo. Don't need to do manually it is completely automatic. Just a single click and get output image with no background. It will increase efficiency & boost workflow.
![](https://github.com/slazzercom/Slazzer-Automatic-Remove-Image-Background-Android/blob/master/screenshot/slazzer_automatic_background_removal_android_app.gif)

### Implementation
Import slazzerlib module library into your project and add below line in your build.gradel file

    implementation project(path: ':slazzerlib')

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

    Slazzer.from(inputFile,object :Slazzer.ResponseCallback{
           
            override fun onProgressStart() {
               //====== Show your progress loader =======
            }
            
            override fun onSuccess(response: String) {
                 val jsonObject=JSONObject(response)
                 if (jsonObject.optBoolean("status"))
                  Picasso.get().load(jsonObject.optString("output_image_url")).into(yourImageView)
            
                 else
                    show(jsonObject.optString("message"))
                
            }
             override fun onProgressUpdate(percentage: Float) {
                runOnUiThread {
                   //====== Display progress percentage =======
                }

            }
            
            override fun onProgressEnd() {
               //====== Hide your progress loader =======
            }
            
            override fun onError(errors: String) {
                //====== Hide loader display error action =======
            }
    })
    
### Error code from response

---
|Response Code | Response | Details |
|---- | ---------- | 
|200     | "output_image_url": "image_url" | Successfully removed image background | 
|400    | "error": "Source image file not found" | Error: Invalid parameters or unable to process input image file (No credit deducted) | 
|401   | "error": "invalid api key"  | Error: API-KEY missing or invalid API-KEY (No credit deducted) |
|402   | "error": "No credits remaining" | Error: No credits remaining (No credit deducted) |
|403   |"error": "Api rate limit crossed" | Error: Api rate limit crossed (No credit deducted)  |
### Sample Project
You can get complete sample code from this repository. Download the project and replace your [API-KEY](https://slazzer.com/api)

### Author
slazzercom
