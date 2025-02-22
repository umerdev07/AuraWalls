package com.example.aurawall

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aurawall.databinding.FragmentQuoteGeneratorBinding
import com.example.quotegeneratorapp.QuoteModelItem
import com.example.quotegeneratorapp.RetrofitInstance
import kotlinx.coroutines.launch
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.IOException
import java.util.Random

class QuoteGeneratorFragment : Fragment() {

    private var _binding: FragmentQuoteGeneratorBinding? = null
    private val binding get() = _binding!!
    private var selectedBackgroundColor = Color.BLACK
    private var selectedTextColor = Color.WHITE
    private var lastSelectedColor = Color.BLACK
    private var selectedTypeface: Typeface? = null
    private var isGradientBackground = false
    private var isGradientText = false
    private var gradientColorsBg = intArrayOf(Color.BLACK, Color.DKGRAY)
    private var gradientColorsText = intArrayOf(Color.WHITE, Color.LTGRAY)
    private var fontSize = 28f
    private val MIN_FONT_SIZE = 16f
    private val MAX_FONT_SIZE = 50f

    private val fontMap = mapOf(
        "Roboto" to R.font.roboto,
        "Montserrat" to R.font.montserrat_subrayada_bold,
        "Dancing Script" to R.font.dancing_script_bold,
        "Nunito" to R.font.nunito_bold,
        "Lato" to R.font.lato_bold,
        "Playfair Display" to R.font.playfair_display_bold,
        "Raleway" to R.font.raleway_bold,
        "Raleway Italic" to R.font.raleway_bold_italic,
        "Source Sans Pro" to R.font.source_sans_pro_bold,
        "Poppins" to R.font.poppins_bold,
        "Merriweather" to R.font.merriweather_bold,
        "Oswald" to R.font.oswald_bold,
        "Ubuntu" to R.font.ubuntu_bold,
        "Lobster" to R.font.lobster
    )

    private val gradientBackgroundOptions = listOf(
        intArrayOf(Color.BLACK, Color.DKGRAY),
        intArrayOf(Color.BLUE, Color.CYAN),
        intArrayOf(Color.RED, Color.YELLOW),
        intArrayOf(Color.GREEN, Color.BLUE),
        intArrayOf(Color.MAGENTA, Color.YELLOW),
        intArrayOf(Color.parseColor("#FF5722"), Color.parseColor("#FFC107")),
        intArrayOf(Color.parseColor("#4CAF50"), Color.parseColor("#CDDC39")),
        intArrayOf(Color.parseColor("#2196F3"), Color.parseColor("#F44336")),
        intArrayOf(Color.parseColor("#9C27B0"), Color.parseColor("#E91E63")),
        intArrayOf(Color.GRAY, Color.WHITE)
    )

    private val gradientTextOptions = listOf(
        intArrayOf(Color.WHITE, Color.LTGRAY),
        intArrayOf(Color.YELLOW, Color.RED),
        intArrayOf(Color.CYAN, Color.BLUE),
        intArrayOf(Color.GREEN, Color.YELLOW),
        intArrayOf(Color.MAGENTA, Color.CYAN),
        intArrayOf(Color.parseColor("#FF9800"), Color.parseColor("#F44336")),
        intArrayOf(Color.parseColor("#4CAF50"), Color.parseColor("#8BC34A")),
        intArrayOf(Color.parseColor("#3F51B5"), Color.parseColor("#2196F3")),
        intArrayOf(Color.parseColor("#E91E63"), Color.parseColor("#9C27B0")),
        intArrayOf(Color.WHITE, Color.parseColor("#B0BEC5"))
    )

    private val storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            downloadQuote()
        } else {
            showToast("Storage permission denied")
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuoteGeneratorBinding.inflate(inflater, container, false)
        setupUI()
        binding.addQuote.setOnClickListener {
            binding.quoteTv.setText(binding.ownQuote.text)
            binding.ownQuote.text.clear()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupUI() {
        setupFontSpinner()
        setupSeekBar()

        // Update title and icon colors
        updateTitleAndIconColors()

        binding.apply {
            btnNextQuote.setOnClickListener { fetchQuote() }
            btnBackgroundColor.setOnClickListener { showColorModeDialog(true) }
            btnTextColor.setOnClickListener { showColorModeDialog(false) }
            applyBtn.setOnClickListener { setWallpaper() }
            downloadBtn.setOnClickListener {
                if (checkStoragePermission()) {
                    downloadQuote()
                } else {
                    requestStoragePermission()
                }
            }
            fontSpinnerTrigger.setOnClickListener {
                fontSpinner.visibility = View.VISIBLE
                fontSpinner.performClick()
                fontSpinner.postDelayed({ fontSpinner.visibility = View.GONE }, 100)
            }
        }
        updateIconTints()
        applyGradientBackground()
        applyGradientText()
    }

    private fun updateTitleAndIconColors() {
        val contrastingColor = getContrastingColor(selectedBackgroundColor, selectedTextColor)
        binding.quoteTitle.setTextColor(contrastingColor)
        binding.addQuote.setColorFilter(contrastingColor, PorterDuff.Mode.SRC_IN)
    }

    private fun setupFontSpinner() {
        val fontNames = fontMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fontNames).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.fontSpinner.adapter = adapter
        binding.fontSpinner.setSelection(0)

        binding.fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fontName = fontNames[position]
                val fontResourceId = fontMap[fontName]
                showToast("Selecting: $fontName (ID: $fontResourceId)")

                if (fontResourceId != null) {
                    try {
                        selectedTypeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
                        if (selectedTypeface != null) {
                            applyFontToText()
                            showToast("Font applied: $fontName")
                        } else {
                            selectedTypeface = Typeface.DEFAULT
                            applyFontToText()
                        }
                    } catch (e: Exception) {
                        selectedTypeface = Typeface.DEFAULT
                        applyFontToText()
                    }
                } else {
                    selectedTypeface = Typeface.DEFAULT
                    applyFontToText()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTypeface = Typeface.DEFAULT
                applyFontToText()
                showToast("No font selected, using default")
            }
        }
    }

    private fun applyFontToText() {
        val typeface = selectedTypeface ?: Typeface.DEFAULT
        binding.quoteTv.setTypeface(typeface)
        binding.quoteAuthor.setTypeface(typeface)

        val quoteText = binding.quoteTv.text.toString()
        val authorText = binding.quoteAuthor.text.toString()
        binding.quoteTv.text = quoteText
        binding.quoteAuthor.text = authorText

        updateTextSize()
        if (isGradientText) applyGradientText()

        binding.quoteTv.invalidate()
        binding.quoteAuthor.invalidate()
        binding.root.post { binding.root.requestLayout() }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSeekBar() {
        binding.seekBar.apply {
            max = (MAX_FONT_SIZE - MIN_FONT_SIZE).toInt()
            progress = (fontSize - MIN_FONT_SIZE).toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    fontSize = MIN_FONT_SIZE + progress
                    binding.fontSizeLabel.text = "Font Size: ${fontSize.toInt()}sp"
                    updateTextSize()
                    if (isGradientText) applyGradientText()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    binding.fontSizeLabel.setTextColor(Color.RED)
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    binding.fontSizeLabel.setTextColor(Color.BLACK)
                }
            })
        }
        binding.fontSizeLabel.text = "Font Size: ${fontSize.toInt()}sp"
    }

    private fun updateTextSize() {
        binding.quoteTv.textSize = fontSize
        binding.quoteAuthor.textSize = fontSize * 0.65f
        binding.quoteTv.invalidate()
        binding.quoteAuthor.invalidate()
    }

    private fun fetchQuote() {
        setInProgress(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.quotesApi.getRandomQuotes()
                response.body()?.first()?.let { setQuoteUI(it) }
            } catch (e: Exception) {
                showToast("Check Internet Connection")
            }
            setInProgress(false)
        }
    }

    private fun setQuoteUI(quote: QuoteModelItem) {
        binding.quoteTv.text = quote.q
        binding.quoteAuthor.text = "- ${quote.a}"
        applyFontToText()
    }

    private fun showColorModeDialog(isBackground: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Color Mode")
            .setItems(arrayOf("Solid Color", "Gradient")) { _, which ->
                when (which) {
                    0 -> openSolidColorPicker(isBackground)
                    1 -> openGradientShadePicker(isBackground)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openSolidColorPicker(isBackground: Boolean) {
        val initialColor = if (isBackground) selectedBackgroundColor else selectedTextColor
        AmbilWarnaDialog(
            requireContext(),
            initialColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    if (isBackground) {
                        isGradientBackground = false
                        selectedBackgroundColor = color
                        binding.root.setBackgroundColor(color)
                        lastSelectedColor = color
                    } else {
                        isGradientText = false
                        selectedTextColor = color
                        binding.quoteTv.setTextColor(color)
                        binding.quoteAuthor.setTextColor(color)
                        lastSelectedColor = color

                        // Clear the shader when switching to solid color
                        binding.quoteTv.paint.shader = null
                        binding.quoteAuthor.paint.shader = null
                    }
                    updateIconTints()
                    updateTitleAndIconColors() // Update title and icon colors
                }
                override fun onCancel(dialog: AmbilWarnaDialog?) {}
            }).show()
    }

    private fun openGradientShadePicker(isBackground: Boolean) {
        val options = if (isBackground) {
            arrayOf(
                "Dark Night", "Ocean Breeze", "Sunset Glow", "Forest Sky", "Neon Flash",
                "Amber Flame", "Lime Forest", "Blue Red Fusion", "Purple Pink", "Silver Light"
            )
        } else {
            arrayOf(
                "Soft Light", "Fiery Glow", "Cool Wave", "Spring Bloom", "Electric Pulse",
                "Orange Red", "Green Shade", "Indigo Blue", "Pink Purple", "White Smoke"
            )
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Gradient Shade")
            .setItems(options) { _, which ->
                if (isBackground) {
                    isGradientBackground = true
                    gradientColorsBg = gradientBackgroundOptions[which]
                    selectedBackgroundColor = gradientColorsBg[0]
                    applyGradientBackground()
                    lastSelectedColor = gradientColorsBg.last()
                } else {
                    isGradientText = true
                    gradientColorsText = gradientTextOptions[which]
                    selectedTextColor = gradientColorsText[0]
                    applyGradientText()
                    lastSelectedColor = gradientColorsText.last()
                }
                updateIconTints()
                updateTitleAndIconColors() // Update title and icon colors
                showToast("Gradient applied: ${options[which]}")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyGradientBackground() {
        if (isGradientBackground) {
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColorsBg
            )
            binding.root.background = gradientDrawable
        }
    }

    private fun applyGradientText() {
        if (isGradientText) {
            val paint = binding.quoteTv.paint
            val shader = LinearGradient(
                0f, 0f, binding.quoteTv.width.toFloat(), binding.quoteTv.textSize,
                gradientColorsText, null, Shader.TileMode.CLAMP
            )
            paint.shader = shader
            binding.quoteTv.invalidate()
            binding.quoteAuthor.paint.shader = shader
            binding.quoteAuthor.invalidate()
        }
    }

    private fun updateIconTints() {
        val iconTint = getContrastingColor(selectedBackgroundColor, selectedTextColor)

        binding.btnTextColor.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
        binding.fontSpinnerTrigger.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
        binding.btnBackgroundColor.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
        binding.btnNextQuote.imageTintList = ColorStateList.valueOf(Color.WHITE)
        binding.downloadBtn.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
        binding.applyBtn.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
    }

    private fun getContrastingColor(bgColor: Int, textColor: Int): Int {
        val bgBrightness = (Color.red(bgColor) * 0.299 + Color.green(bgColor) * 0.587 + Color.blue(bgColor) * 0.114)
        val textBrightness = (Color.red(textColor) * 0.299 + Color.green(textColor) * 0.587 + Color.blue(textColor) * 0.114)

        if (bgColor == textColor) {
            return if (bgBrightness > 128) Color.BLACK else Color.WHITE
        }

        val bgContrast = if (bgBrightness > 128) Color.BLACK else Color.WHITE
        val textContrast = if (textBrightness > 128) Color.BLACK else Color.WHITE

        return if (isVisibleAgainst(bgContrast, textColor)) {
            bgContrast
        } else {
            if (isVisibleAgainst(textContrast, bgColor)) textContrast else Color.GRAY
        }
    }

    private fun isVisibleAgainst(tint: Int, againstColor: Int): Boolean {
        val tintBrightness = (Color.red(tint) * 0.299 + Color.green(tint) * 0.587 + Color.blue(tint) * 0.114)
        val againstBrightness = (Color.red(againstColor) * 0.299 + Color.green(againstColor) * 0.587 + Color.blue(againstColor) * 0.114)
        return kotlin.math.abs(tintBrightness - againstBrightness) > 50
    }

    private fun downloadQuote() {
        val bitmap = createQuoteBitmap()
        saveImage(bitmap)
    }

    private fun saveImage(image: Bitmap) {
        try {
            val random1 = Random().nextInt(520985)
            val random2 = Random().nextInt(952663)
            val name = "Aura-$random1$random2.jpg"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AuraWalls")
            }

            requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                showToast("Image Saved")
            } ?: throw IOException("Failed to create new MediaStore record")
        } catch (e: IOException) {
            showToast("Image Not Saved: ${e.message}")
        } catch (e: Exception) {
            showToast("Something Wrong: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        val bitmap = createQuoteBitmap()
        try {
            wallpaperManager.setBitmap(bitmap)
            showToast("Wallpaper Set Successfully!")
        } catch (e: Exception) {
            showToast("Failed to Set Wallpaper: ${e.message}")
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            downloadQuote()
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        binding.progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        binding.applyBtn.visibility = if (inProgress) View.GONE else View.VISIBLE
        binding.downloadBtn.visibility = if (inProgress) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createQuoteBitmap(): Bitmap {
        // Get device screen dimensions
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Apply background
        if (isGradientBackground) {
            val gradientPaint = Paint().apply {
                shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    gradientColorsBg, null, Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradientPaint)
        } else {
            canvas.drawColor(selectedBackgroundColor)
        }

        // Adjust text size based on screen width (responsive scaling)
        val baseTextSize = width / 20f // Adjust divisor for desired text size scaling
        val textMaxWidth = width - (2 * (width / 20f)) // Padding = 5% of width

        // Create TextPaint with contrasting color or gradient
        val textPaint = createTextPaint(selectedTextColor, baseTextSize, textMaxWidth.toInt(), isGradientText)
        val authorPaint = createTextPaint(selectedTextColor, baseTextSize * 0.65f, textMaxWidth.toInt(), isGradientText)

        // Create layouts for quote and author
        val quoteLayout = createTextLayout(binding.quoteTv.text.toString(), textPaint, textMaxWidth.toInt())
        val authorLayout = createTextLayout(binding.quoteAuthor.text.toString(), authorPaint, textMaxWidth.toInt())

        // Calculate total height of text block (quote + author + spacing)
        val totalTextHeight = quoteLayout.height + authorLayout.height + (height / 20f) // 5% spacing
        val startY = (height - totalTextHeight) / 2f // Center vertically

        // Draw quote and author centered
        canvas.apply {
            save()
            translate((width - quoteLayout.width) / 2f, startY)
            quoteLayout.draw(this)
            restore()

            save()
            translate((width - authorLayout.width) / 2f, startY + quoteLayout.height + (height / 20f))
            authorLayout.draw(this)
            restore()
        }

        return bitmap
    }

    private fun createTextPaint(
        color: Int,
        baseSize: Float,
        maxWidth: Int,
        isGradient: Boolean
    ): TextPaint {
        val paint = TextPaint().apply {
            this.color = color
            textSize = baseSize
            typeface = selectedTypeface ?: Typeface.DEFAULT_BOLD
            isAntiAlias = true
            // Add shadow layer for visibility when not using gradient
            if (!isGradient) {
                setShadowLayer(2f, 1f, 1f, getContrastingColor(selectedBackgroundColor, color))
            }
        }
        if (isGradient) {
            paint.shader = LinearGradient(
                0f, 0f, maxWidth.toFloat(), 0f,
                gradientColorsText, null, Shader.TileMode.CLAMP
            )
        }
        return paint
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createTextLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(10f, 1f)
            .setIncludePad(true)
            .setMaxLines(5)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
    }
}