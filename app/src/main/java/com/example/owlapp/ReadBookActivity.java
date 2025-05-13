package com.example.owlapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReadBookActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvChapterTitle;
    private TextView tvContent;
    private ImageButton btnBack;
    private ImageButton btnMenu;
    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private ImageButton btnBookmark;
    private ImageButton btnSettings;
    private ScrollView scrollView;

    private int currentChapter = 1;
    private int totalChapters = 10;
    private boolean isNightMode = false;
    private float currentTextSize = 16f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        initViews();
        setupListeners();
        loadChapter(currentChapter);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvContent = findViewById(R.id.tvContent);
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnBookmark = findViewById(R.id.btnBookmark);
        btnSettings = findViewById(R.id.btnSettings);
        scrollView = findViewById(R.id.contentScrollView);

        // Debug log to check if scrollView is null
        if (scrollView == null) {
            System.out.println("WARNING: ScrollView is null after findViewById");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChapterSelectionDialog();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChapter > 1) {
                    loadChapter(--currentChapter);
                } else {
                    Toast.makeText(ReadBookActivity.this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChapter < totalChapters) {
                    loadChapter(++currentChapter);
                } else {
                    showFinishReadingDialog();
                }
            }
        });

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReadBookActivity.this, "Đã thêm bookmark", Toast.LENGTH_SHORT).show();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });
    }

    private void loadChapter(int chapter) {
        tvChapterTitle.setText("Chương " + chapter + ": " + getChapterTitle(chapter));
        tvContent.setText(getChapterContent(chapter));

        // Add null check before using scrollView
        if (scrollView != null) {
            scrollView.scrollTo(0, 0);
        }
    }

    private String getChapterTitle(int chapter) {
        switch (chapter) {
            case 1:
                return "Cha Giàu Quá !!";
            case 2:
                return "Bài Học Đầu Tiên";
            case 3:
                return "Bắt Đầu Suy Nghĩ Như Người Giàu";
            default:
                return "Chương " + chapter;
        }
    }

    private String getChapterContent(int chapter) {
        switch (chapter) {
            case 1:
                return "\"Tôi có hai người cha, một người giàu và một người nghèo - một người cha ruột và một người cha nuôi (cha của Mike - bạn tôi). Cha ruột tôi đã có bằng thạc sĩ, còn người cha nuôi thì chưa học hết lớp tám, nhưng cả hai người đều thành công trong sự nghiệp và có ảnh hưởng đến người khác.\"\n\nCuốn Cha giàu cha nghèo là một ngôi sao sáng lấp lánh và xuất sắc nhất trong bộ sách được xuất bản mang tên Dạy con làm giàu gồm mười ba cuốn của nhà văn. Nội dung chủ yếu cuốn sách là có thể tiếp thu những bài học đầu tư toàn kiến thức chuyên ngành thâm sâu, khó hiểu mà là những bài học gần gũi mà tác giả nhận được từ hai người cha của mình: Người cha ruột tuy nghèo và có một tư duy theo lối mòn, cổ kỉ và hay than vãn. Người cha của bạn thân, người sở hữu tài sản kếch xù với đầu óc đầu tư nhạy bén, cực kì sắc sảo và lối tư duy rất hiện đại, mới mẻ. Hai lối tư duy và hai cuộc đời tưởng chừng đối lập ấy lại phản chiếu hai hóa qua cậu bé là tác giả của những năm tháng ấy và là một phần trong tư duy đầu tư và kiếm tiền của ông sau này. Một câu hỏi cấp hai năm đó, mặc dù cực kì ngưỡng mộ và mong muốn học cách trở nên tư như người Cha giàu nhưng cũng không hề ghét bỏ, vẫn luôn kiên nhẫn lắng nghe và ghi nhớ những lời của người Cha nghèo của mình.";
            case 2:
                return "Bài học đầu tiên tôi học được từ cha giàu là người giàu không làm việc vì tiền. Đa số mọi người làm việc vì tiền, nhưng người giàu làm cho tiền làm việc cho họ.\n\nKhi còn là một cậu bé 9 tuổi, tôi và người bạn thân Mike đã bắt đầu học về tiền bạc từ cha của Mike - người mà sau này tôi gọi là \"cha giàu\". Chúng tôi muốn biết làm thế nào để kiếm tiền, và cha giàu đã dạy chúng tôi một bài học quan trọng.\n\nÔng đã thuê chúng tôi làm việc trong cửa hàng của mình với mức lương rất thấp - chỉ 10 xu một giờ. Sau vài tuần làm việc chăm chỉ, tôi cảm thấy không hài lòng và muốn bỏ việc. Đó chính xác là điều cha giàu mong đợi.\n\nÔng giải thích rằng hầu hết mọi người chấp nhận công việc với mức lương thấp vì họ sợ không có tiền và để đáp ứng nhu cầu tức thời. Họ trở thành nô lệ của đồng tiền, làm việc, kiếm tiền, chi tiêu, và rồi lại cần nhiều tiền hơn. Đó là \"Vòng luẩn quẩn của người nghèo\".";
            case 3:
                return "Cha giàu dạy tôi rằng cách suy nghĩ của bạn về tiền bạc sẽ quyết định tương lai tài chính của bạn. Ông thường nói: \"Người nghèo và tầng lớp trung lưu làm việc vì tiền. Người giàu khiến tiền làm việc cho họ.\"\n\nÔng giải thích sự khác biệt giữa tài sản và tiêu sản. Tài sản là những thứ đưa tiền vào túi bạn, trong khi tiêu sản lấy tiền ra khỏi túi bạn. Người giàu tích lũy tài sản, trong khi người nghèo và tầng lớp trung lưu tích lũy tiêu sản mà họ nghĩ là tài sản.\n\nVí dụ, một ngôi nhà thường được coi là tài sản, nhưng thực tế nó có thể là một tiêu sản lớn nếu bạn phải trả khoản vay thế chấp, thuế, bảo hiểm và bảo trì. Thay vào đó, người giàu đầu tư vào những thứ tạo ra dòng tiền như bất động sản cho thuê, cổ phiếu trả cổ tức, hoặc kinh doanh không đòi hỏi sự hiện diện của họ.\n\nĐể bắt đầu suy nghĩ như người giàu, bạn cần tập trung vào việc tăng tài sản thay vì tăng thu nhập. Thu nhập thường bị đánh thuế cao hơn và dễ bị tiêu hết, trong khi tài sản có thể tiếp tục phát triển và tạo ra nhiều thu nhập hơn.";
            default:
                return "Nội dung chương " + chapter + " đang được cập nhật...";
        }
    }

    private void showChapterSelectionDialog() {
        String[] chapters = new String[totalChapters];
        for (int i = 0; i < totalChapters; i++) {
            chapters[i] = "Chương " + (i + 1) + ": " + getChapterTitle(i + 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn chương")
                .setItems(chapters, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        currentChapter = which + 1;
                        loadChapter(currentChapter);
                    }
                });
        builder.create().show();
    }

    private void showSettingsDialog() {
        String[] options = {"Tăng cỡ chữ", "Giảm cỡ chữ", "Chế độ ban đêm", "Chế độ ban ngày"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cài đặt")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                increaseTextSize();
                                break;
                            case 1:
                                decreaseTextSize();
                                break;
                            case 2:
                                enableNightMode();
                                break;
                            case 3:
                                disableNightMode();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void increaseTextSize() {
        if (currentTextSize < 24f) {
            currentTextSize += 2f;
            tvContent.setTextSize(currentTextSize);
        }
    }

    private void decreaseTextSize() {
        if (currentTextSize > 12f) {
            currentTextSize -= 2f;
            tvContent.setTextSize(currentTextSize);
        }
    }

    private void enableNightMode() {
        if (!isNightMode) {
            isNightMode = true;
            if (scrollView != null) {
                scrollView.setBackgroundColor(0xFF121212);
            }
            tvContent.setTextColor(0xFFEEEEEE);
        }
    }

    private void disableNightMode() {
        if (isNightMode) {
            isNightMode = false;
            if (scrollView != null) {
                scrollView.setBackgroundColor(0xFFFFFFFF);
            }
            tvContent.setTextColor(0xFF000000);
        }
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thoát")
                .setMessage("Bạn có muốn thoát khỏi chế độ đọc sách?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showFinishReadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kết thúc")
                .setMessage("Bạn muốn kết thúc đọc tại đây 👋👋👋?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
}
