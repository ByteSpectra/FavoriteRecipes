package com.mobivone.favrecipes.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.ActivityAddUpdateRecipesBinding
import com.mobivone.favrecipes.databinding.DialogCustomImageSelectionBinding
import com.mobivone.favrecipes.databinding.DialogCustomListBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.utils.Constants
import com.mobivone.favrecipes.utils.FavDishApplication
import com.mobivone.favrecipes.view.adapters.CustomListItemAdapter
import com.mobivone.favrecipes.viewModel.FavDishViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddUpdateRecipes : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateRecipesBinding
    private var mImagePath: String = ""

    private lateinit var mCustomListDialog: Dialog

    private var mFavDishDetail: FavDish? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateRecipesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS))
            mFavDishDetail = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)

        setupActionBar()

        mBinding.ivAddImage.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddUpdateDish.setOnClickListener(this)
    }

    private fun setupActionBar()
    {
        setSupportActionBar(mBinding.toolbar)

        if (mFavDishDetail != null && mFavDishDetail!!.id != 0)
        {
            supportActionBar?.let {
                it.title = resources.getString(R.string.adpater_menu_edit_dish)
            }

            mFavDishDetail?.let {

                mImagePath = it.image

                Glide.with(this@AddUpdateRecipes)
                    .load(mImagePath)
                    .into(mBinding.ivImage)

                mBinding.etTitle.setText(it.title)
                mBinding.etType.setText(it.type)
                mBinding.etCategory.setText(it.category)
                mBinding.etIngredients.setText(it.ingredients)
                mBinding.etCookingTime.setText(it.cookingTime)
                mBinding.etDirection.setText(it.directionToCook)

                mBinding.btnAddUpdateDish.text = resources.getString(R.string.lbl_update_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when(v.id)
        {
            R.id.iv_addImage -> {
                customImageSelectionDialog()
                return
            }
            R.id.et_type -> {
                customListDialog(resources.getString(R.string.title_select_dish_type),
                Constants.dishTypes(), Constants.DISTH_TYPE)
                return
            }
            R.id.et_category -> {
                customListDialog(resources.getString(R.string.title_select_dish_category),
                Constants.dishCategories(), Constants.DISTH_CATEGORY)
                return
            }
            R.id.et_cookingTime -> {
                customListDialog(resources.getString(R.string.title_select_dish_cookingTime),
                Constants.dishCookTime(), Constants.DISTH_COOKING_TIME)
                return
            }
            R.id.btn_addUpdateDish -> {
                val title = mBinding.etTitle.text.toString().trim{ it <= ' ' }
                val type = mBinding.etType.text.toString().trim{ it <= ' ' }
                val category = mBinding.etCategory.text.toString().trim{ it <= ' ' }
                val ingredients = mBinding.etIngredients.text.toString().trim{ it <= ' ' }
                val cookingTime = mBinding.etCookingTime.text.toString().trim{ it <= ' ' }
                val cookingDirection = mBinding.etDirection.text.toString().trim{ it <= ' ' }

                when {
                    TextUtils.isEmpty(mImagePath) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_image), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(title) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_title), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(type) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_type), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(category) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_category), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(ingredients) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_ingredients), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(cookingTime) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_cookingTime), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(cookingDirection) -> {
                        Toast.makeText(this, resources.getString(R.string.err_msg_select_directions), Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                        var dishID = 0
                        var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                        var favoriteDish = false

                        mFavDishDetail.let {
                            if (mFavDishDetail!!.id != 0)
                            {
                                dishID = it!!.id
                                imageSource = it.imageSource
                                favoriteDish = it.favoriteDish
                            }
                        }
                        val model = FavDish(
                            mImagePath,
                            imageSource,
                            title,
                            type,
                            category,
                            ingredients,
                            cookingTime,
                            cookingDirection,
                            favoriteDish,
                            dishID
                        )

                        if (dishID == 0) {
                            mFavDishViewModel.insert(model)
                            Toast.makeText(this@AddUpdateRecipes, "Successfully Added", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            mFavDishViewModel.update(model)
                            Toast.makeText(this@AddUpdateRecipes, "Successfully Updated", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                }
            }

        }
    }

    private fun customImageSelectionDialog()
    {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
               // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()){
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA)
                        }
                        else
                            showRationaleDialogForPermission()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationaleDialogForPermission()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }

        binding.ivGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    response?.let {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddUpdateRecipes, "You have denied gallery permission", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationaleDialogForPermission()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showRationaleDialogForPermission()
    {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions required for this feature."+
                                "It can be enable under Application Settings")
            .setPositiveButton("Go to Settings")
            {_,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA){
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    //mBinding.ivImage.setImageBitmap(thumbnail)
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)
                    mBinding.ivAddImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_24))
                }
            }
            else if (requestCode == GALLERY){
                data?.let {
                    val selectedPhotoUri = data.data
                    //mBinding.ivImage.setImageURI(selectedPhotoUri)
                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object: RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG", "Error Loading Image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                }
                                return false
                            }

                        })
                        .into(mBinding.ivImage)
                    mBinding.ivAddImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_24))
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(this@AddUpdateRecipes, "Action Canceled", Toast.LENGTH_SHORT).show()
            Log.e("Cancelled", "User canceled the operation")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String
    {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGES_DIRECTORY, MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customListDialog(title: String, itemList: List<String>, selection: String)
    {
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, null, itemList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    fun selectedListItem(item: String, selection: String)
    {
        when(selection) {
            Constants.DISTH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISTH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }

    private companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGES_DIRECTORY = "FavRecipesImages"
    }
}